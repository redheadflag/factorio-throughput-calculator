package io.github.redheadflag.engine;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.swing.SwingUtilities;

import mpi.MPI;
import mpi.Request;

import io.github.redheadflag.distribution.BoundaryEdges;
import io.github.redheadflag.distribution.Edge;
import io.github.redheadflag.distribution.Partitioning;
import io.github.redheadflag.distribution.Worker;
import io.github.redheadflag.tiles.Tile;
import io.github.redheadflag.ui.GamePanel;
import io.github.redheadflag.ui.GameWindow;
import io.github.redheadflag.ui.StatisticsWindow;
import io.github.redheadflag.ui.TemplateSelectionWindow;
import io.github.redheadflag.world.GameGrid;
import io.github.redheadflag.world.Inventory;
import io.github.redheadflag.world.Resource;
import io.github.redheadflag.world.ResourceType;
import io.github.redheadflag.world.TickContext;
import io.github.redheadflag.world.TransferService;

public class Main {

    private static final int NOTHING = -1;

    public static void main(String[] args) throws Exception {
        args = MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        String gridPath = rank == 0 ? TemplateSelectionWindow.showAndWait() : null;

        int[] pathLength = new int[1];
        byte[] pathBytes = rank == 0 ? gridPath.getBytes(StandardCharsets.UTF_8) : null;
        if (rank == 0) {
            pathLength[0] = pathBytes.length;
        }
        MPI.COMM_WORLD.Bcast(pathLength, 0, 1, MPI.INT, 0);

        if (rank != 0) {
            pathBytes = new byte[pathLength[0]];
        }
        MPI.COMM_WORLD.Bcast(pathBytes, 0, pathLength[0], MPI.BYTE, 0);

        if (rank != 0) {
            gridPath = new String(pathBytes, StandardCharsets.UTF_8);
        }

        int[] ticksPerSecondBuf = { rank == 0 ? TemplateSelectionWindow.getSelectedTicksPerSecond() : 0 };
        MPI.COMM_WORLD.Bcast(ticksPerSecondBuf, 0, 1, MPI.INT, 0);
        int ticksPerSecond = ticksPerSecondBuf[0];

        long[] seedBuf = { rank == 0 ? TemplateSelectionWindow.getSelectedSeed() : 0L };
        MPI.COMM_WORLD.Bcast(seedBuf, 0, 1, MPI.LONG, 0);
        long seed = seedBuf[0];

        GameGrid grid = GameGrid.fromFile(gridPath);
        Worker myWorker = Worker.buildAll(grid, size).get(rank);

        List<Edge> pushEdges = new ArrayList<>();
        for (Edge e : BoundaryEdges.compute(grid, size)) {
            if (e.type() == BoundaryEdges.MovementType.PUSH) {
                pushEdges.add(e);
            }
        }

        Set<Tile> myBoundarySources = new HashSet<>();
        for (Edge e : pushEdges) {
            if (e.sourcePartition() == rank) {
                myBoundarySources.add(e.source());
            }
        }

        List<Tile[]> localTransferPairs = new ArrayList<>();
        for (Tile source : myBoundarySources) {
            for (Tile target : source.getPushTargets()) {
                if (target == null) {
                    continue;
                }
                boolean isRemoteEdge = pushEdges.stream()
                    .anyMatch(e -> e.source() == source && e.target() == target && e.sourcePartition() == rank);
                if (!isRemoteEdge) {
                    localTransferPairs.add(new Tile[] { source, target });
                }
            }
        }

        List<Edge> outgoingEdges = new ArrayList<>();
        List<Integer> outgoingTags = new ArrayList<>();
        List<Edge> incomingEdges = new ArrayList<>();
        List<Integer> incomingTags = new ArrayList<>();

        for (int i = 0; i < pushEdges.size(); i++) {
            Edge e = pushEdges.get(i);
            if (e.sourcePartition() == rank) {
                outgoingEdges.add(e);
                outgoingTags.add(i);
            }
            if (e.targetPartition() == rank) {
                incomingEdges.add(e);
                incomingTags.add(i);
            }
        }

        int resourceTypeCount = ResourceType.values().length;

        List<List<Tile>> allPartitions = new Partitioning(grid, size).compute();
        List<Tile> myGatherTiles = allPartitions.get(rank);

        int[] gatherCounts = new int[size];
        int[] gatherDispls = new int[size];
        int running = 0;
        for (int r = 0; r < size; r++) {
            gatherCounts[r] = allPartitions.get(r).size() * resourceTypeCount;
            gatherDispls[r] = running;
            running += gatherCounts[r];
        }

        List<Tile> allTilesInRankOrder = null;
        GamePanel panel = null;
        if (rank == 0) {
            allTilesInRankOrder = new ArrayList<>();
            for (List<Tile> partitionTiles : allPartitions) {
                allTilesInRankOrder.addAll(partitionTiles);
            }
            panel = new GamePanel(grid);
            GameWindow.show(panel);
        }

        TransferService localTransfer = new TransferService();
        TickContext ctx = new TickContext(seed);
        long tickPeriodMs = 1000L / Math.max(1, ticksPerSecond);
        long start = System.nanoTime();
        while (!ctx.checkEndCondition()) {
            ctx.incrTickCount();

            boolean anyUpdate = myWorker.tick(ctx);

            for (Tile[] pair : localTransferPairs) {
                if (localTransfer.transferOne(pair[0], pair[1], ctx)) {
                    anyUpdate = true;
                }
            }

            boolean[] settled = new boolean[outgoingEdges.size()];

            boolean keepGoing;
            do {
                boolean progress = runRound(outgoingEdges, outgoingTags, incomingEdges, incomingTags, settled, ctx);
                anyUpdate |= progress;

                int[] local = { progress ? 1 : 0 };
                int[] global = new int[1];
                MPI.COMM_WORLD.Allreduce(local, 0, global, 0, 1, MPI.INT, MPI.MAX);
                keepGoing = global[0] != 0;
            } while (keepGoing);

            int[] localUpdate = { anyUpdate ? 1 : 0 };
            int[] globalUpdate = new int[1];
            MPI.COMM_WORLD.Allreduce(localUpdate, 0, globalUpdate, 0, 1, MPI.INT, MPI.MAX);

            if (globalUpdate[0] != 0) {
                ctx.logUpdate();
            }

            int[] sendBuf = new int[myGatherTiles.size() * resourceTypeCount];
            for (int i = 0; i < myGatherTiles.size(); i++) {
                Map<ResourceType, Long> counts = myGatherTiles.get(i).getInventory().countByType();
                for (ResourceType rt : ResourceType.values()) {
                    sendBuf[i * resourceTypeCount + rt.ordinal()] = counts.getOrDefault(rt, 0L).intValue();
                }
            }

            int totalLen = rank == 0 ? gatherDispls[size - 1] + gatherCounts[size - 1] : 0;
            int[] recvBuf = new int[Math.max(totalLen, 1)];
            MPI.COMM_WORLD.Gatherv(sendBuf, 0, sendBuf.length, MPI.INT, recvBuf, 0, gatherCounts, gatherDispls, MPI.INT, 0);

            if (rank == 0) {
                for (int i = 0; i < allTilesInRankOrder.size(); i++) {
                    Inventory inv = allTilesInRankOrder.get(i).getInventory();
                    while (!inv.isEmpty()) {
                        inv.removeFirst();
                    }
                    for (ResourceType rt : ResourceType.values()) {
                        int count = recvBuf[i * resourceTypeCount + rt.ordinal()];
                        for (int k = 0; k < count; k++) {
                            inv.add(rt);
                        }
                    }
                }
                GamePanel panelToRepaint = panel;
                SwingUtilities.invokeLater(panelToRepaint::repaint);
            }

            Thread.sleep(tickPeriodMs);
        }

        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        System.out.println("rank " + rank + "/" + size + " done at tick " + ctx.tickCount() + " elapsedMs=" + elapsedMs);

        if (rank == 0) {
            SwingUtilities.invokeLater(() -> StatisticsWindow.show(ctx, grid));
        }

        MPI.Finalize();
    }

    private static boolean runRound(
        List<Edge> outgoingEdges,
        List<Integer> outgoingTags,
        List<Edge> incomingEdges,
        List<Integer> incomingTags,
        boolean[] settled,
        TickContext ctx
    ) throws Exception {
        boolean progress = false;

        int n = outgoingEdges.size();
        int[][] outBufs = new int[n][1];
        Resource[] outPeeked = new Resource[n];
        Request[] outSendReqs = new Request[n];

        for (int i = 0; i < n; i++) {
            Edge e = outgoingEdges.get(i);
            Inventory inv = e.source().getInventory();
            Optional<Resource> peek = inv.peekFirst();
            boolean canPush = !settled[i]
                && peek.isPresent()
                && !peek.get().movedThisTick(ctx.tickCount())
                && inv.getPolicy().canExtract(inv, peek.get().type);

            outPeeked[i] = canPush ? peek.get() : null;
            outBufs[i][0] = canPush ? peek.get().type.ordinal() : NOTHING;
            outSendReqs[i] = MPI.COMM_WORLD.Isend(outBufs[i], 0, 1, MPI.INT, e.targetPartition(), 2 * outgoingTags.get(i));
        }

        int m = incomingEdges.size();
        int[][] inBufs = new int[m][1];
        Request[] inRecvReqs = new Request[m];
        for (int i = 0; i < m; i++) {
            Edge e = incomingEdges.get(i);
            inRecvReqs[i] = MPI.COMM_WORLD.Irecv(inBufs[i], 0, 1, MPI.INT, e.sourcePartition(), 2 * incomingTags.get(i));
        }
        if (m > 0) {
            Request.Waitall(inRecvReqs);
        }

        List<Request> ackSendReqs = new ArrayList<>();
        int[][] ackSendBufs = new int[m][1];
        for (int i = 0; i < m; i++) {
            Edge e = incomingEdges.get(i);
            int msg = inBufs[i][0];
            if (msg == NOTHING) {
                continue;
            }

            boolean accepted = false;
            Resource res = new Resource(ResourceType.values()[msg]);
            Inventory targetInv = e.target().getInventory();
            if (targetInv.canAdd(res)) {
                targetInv.add(res);
                accepted = true;
            }

            ackSendBufs[i][0] = accepted ? 1 : 0;
            ackSendReqs.add(MPI.COMM_WORLD.Isend(ackSendBufs[i], 0, 1, MPI.INT, e.sourcePartition(), 2 * incomingTags.get(i) + 1));
            if (accepted) {
                progress = true;
            }
        }

        List<Integer> awaitingAck = new ArrayList<>();
        List<Request> ackRecvReqs = new ArrayList<>();
        int[][] ackRecvBufs = new int[n][1];
        for (int i = 0; i < n; i++) {
            if (outPeeked[i] != null) {
                Edge e = outgoingEdges.get(i);
                ackRecvReqs.add(MPI.COMM_WORLD.Irecv(ackRecvBufs[i], 0, 1, MPI.INT, e.targetPartition(), 2 * outgoingTags.get(i) + 1));
                awaitingAck.add(i);
            }
        }

        if (n > 0) {
            Request.Waitall(outSendReqs);
        }
        if (!ackSendReqs.isEmpty()) {
            Request.Waitall(ackSendReqs.toArray(new Request[0]));
        }
        if (!ackRecvReqs.isEmpty()) {
            Request.Waitall(ackRecvReqs.toArray(new Request[0]));
        }

        for (int i : awaitingAck) {
            if (ackRecvBufs[i][0] == 1) {
                Edge e = outgoingEdges.get(i);
                Inventory inv = e.source().getInventory();
                inv.removeFirst();
                outPeeked[i].markMoved(ctx.tickCount());
                settled[i] = true;
                progress = true;
            }
        }

        return progress;
    }
}
