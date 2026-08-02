package io.github.redheadflag.distribution;

import java.util.ArrayList;
import java.util.List;

import io.github.redheadflag.tiles.Tile;
import io.github.redheadflag.world.GameGrid;

public class Partitioning {

    private final GameGrid grid;
    private final int numPartitions;

    public Partitioning(GameGrid grid, int numPartitions) {
        this.grid = grid;
        this.numPartitions = numPartitions;
    }
    public int bandHeight() {
        return (grid.getHeight() + numPartitions - 1) / numPartitions;
    }

    public int partitionIndexOf(int y) {
        int band = bandHeight();
        return Math.min(y / band, numPartitions - 1);
    }

    public List<List<Tile>> compute() {
        List<List<Tile>> partitions = new ArrayList<>();
        for (int i = 0; i < numPartitions; i++) {
            partitions.add(new ArrayList<>());
        }
        for (int y = 0; y < grid.getHeight(); y++) {
            int partitionIndex = partitionIndexOf(y);
            for (int x = 0; x < grid.getWidth(); x++) {
                Tile t = grid.getTileAt(x, y);
                if (t != null) {
                    partitions.get(partitionIndex).add(t);
                }
            }
        }
        return partitions;
    }
}
