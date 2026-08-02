package io.github.redheadflag.distribution;

import java.util.ArrayList;
import java.util.List;

import io.github.redheadflag.tiles.Tile;
import io.github.redheadflag.world.GameGrid;

public class BoundaryEdges {
    public enum MovementType {PUSH, FORWARD};

    public static List<Edge> compute(GameGrid grid, int numPartitions) {
        Partitioning partitioning = new Partitioning(grid, numPartitions);
        List<Edge> edges = new ArrayList<>();
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                Tile t = grid.getTileAt(x, y);
                if (t == null) continue;
                int sourcePartition = partitioning.partitionIndexOf(t.getY());
                collect(edges, t, sourcePartition, t.getPushTargets(), MovementType.PUSH, partitioning);
                collect(edges, t, sourcePartition, t.getForwardTargets(), MovementType.FORWARD, partitioning);
            }
        }
        return edges;
    }

    private static void collect(
        List<Edge> edges,
        Tile source,
        int sourcePartition,
        List<Tile> targets,
        MovementType type,
        Partitioning partitioning 
    ) {
        for (Tile target : targets) {
            if (target == null) continue;
            int targetPartition = partitioning.partitionIndexOf(target.getY());
            if (targetPartition != sourcePartition) {
                edges.add(new Edge(source, target, sourcePartition, targetPartition, type));
            }
        }
    }
}
