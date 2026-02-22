package io.github.redheadflag.world;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

import io.github.redheadflag.tiles.ConveyorBeltTile;
import io.github.redheadflag.tiles.SplitterTile;
import io.github.redheadflag.tiles.Tile;

public class TransferService {
    public boolean transferOne(Tile fromTile, Tile toTile, TickContext tickContext) {
        return transferOneRecursive(fromTile, toTile, tickContext, new ArrayDeque<>());
    }

    private boolean transferOneRecursive(
        Tile fromTile,
        Tile toTile,
        TickContext tickContext,
        Deque<Tile> path
    ) {
        if (fromTile == null || toTile == null) return false;

        Inventory from = fromTile.getInventory();
        Inventory to = toTile.getInventory();
        if (from == null || to == null) return false;

        if (path.contains(fromTile)) return false;
        path.push(fromTile);

        try {
            Optional<Resource> opt = from.peekFirst();
            if (opt.isEmpty()) return false;

            Resource res = opt.get();

            if (res.movedThisTick(tickContext.tickCount())) return false;
            if (!from.getPolicy().canExtract(from, res.type)) return false;

            if (to.canAdd(res)) {
                return commitTransfer(from, to, res, tickContext);
            }

            if (!makeRoomInTarget(toTile, tickContext, path)) return false;
            if (!to.canAdd(res)) return false;

            return commitTransfer(from, to, res, tickContext);
        } finally {
            path.pop();
        }
    }

    private boolean makeRoomInTarget(Tile targetTile, TickContext tickContext, Deque<Tile> path) {
        List<Tile> forwardTargets = getForwardTargets(targetTile);
        for (Tile next : forwardTargets) {
            if (transferOneRecursive(targetTile, next, tickContext, path)) {
                return true;
            }
        }
        return false;
    }

    private List<Tile> getForwardTargets(Tile tile) {
        List<Tile> targets = new ArrayList<>();

        if (tile instanceof ConveyorBeltTile belt) {
            targets.add(belt.getNeighbourTile(belt.getDirection()));
            return targets;
        }

        if (tile instanceof SplitterTile splitter) {
            for (Tile target : splitter.getOutputTilesByPriority()) {
                targets.add(target);
            }
            return targets;
        }

        return targets;
    }

    private boolean commitTransfer(Inventory from, Inventory to, Resource res, TickContext tickContext) {
        Optional<Resource> removed = from.removeFirst();
        if (removed.isEmpty()) return false;

        boolean added = to.add(res);
        if (!added) {
            from.add(removed.get());
            return false;
        }

        res.markMoved(tickContext.tickCount());
        tickContext.logUpdate();
        return true;
    }

    public boolean transferOne(Inventory from, Inventory to, TickContext tickContext) {
        Optional<Resource> opt = from.peekFirst();
        if (opt.isEmpty()) return false;

        Resource res = opt.get();

        // Your rule: an item moved this tick cannot move again
        if (res.movedThisTick(tickContext.tickCount())) return false;
        if (!from.getPolicy().canExtract(from, res.type)) return false;

        if (!to.canAdd(res)) return false;

        // Now commit
        from.removeFirst();
        to.add(res);
        res.markMoved(tickContext.tickCount());
        tickContext.logUpdate();
        return true;
    }
}
