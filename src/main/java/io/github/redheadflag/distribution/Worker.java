package io.github.redheadflag.distribution;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.redheadflag.tiles.Tile;
import io.github.redheadflag.world.GameGrid;
import io.github.redheadflag.world.TickContext;
import io.github.redheadflag.world.Updatable;

public class Worker {
    private final List<Tile> tiles;
    private final Set<Tile> boundarySources;

    public Worker(List<Tile> tiles, Set<Tile> boundarySources) {
        this.tiles = tiles;
        this.boundarySources = boundarySources;
    }

    public List<Tile> tiles() {
        return tiles;
    }

    public boolean tick(TickContext tickContext) {
        boolean isUpdated = false;
        for (Tile t : tiles) {
            if (boundarySources.contains(t))
                continue;
            if (((Updatable) t).tick(tickContext))
                isUpdated = true;
        }
        return isUpdated;
    }

    public static List<Worker> buildAll(GameGrid grid, int numPartitions) {
        List<List<Tile>> partitions = new Partitioning(grid, numPartitions).compute();
        List<Edge> edges = BoundaryEdges.compute(grid, numPartitions);

        Set<Tile> boundarySources = new HashSet<>();
        for (Edge edge : edges) {
            boundarySources.add(edge.source());
        }

        List<Worker> workers = new ArrayList<>();
        for (List<Tile> partitionTiles : partitions) {
            List<Tile> updatable = new ArrayList<>();
            for (Tile t : partitionTiles) {
                if (t instanceof Updatable) {
                    updatable.add(t);
                }
            }
            workers.add(new Worker(updatable, boundarySources));
        }
        return workers;
    }
}
