package io.github.redheadflag.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.github.redheadflag.tiles.Tile;

public final class TileComponents {

    private final GameGrid grid;

    public TileComponents(GameGrid grid) {
        this.grid = grid;
    }

    public List<List<Tile>> compute() {
        Map<Tile, Tile> parent = initSingletons();
        unionPushEdges(parent);
        return groupByRoot(parent);
    }

    private Map<Tile, Tile> initSingletons() {
        Map<Tile, Tile> parent = new HashMap<>();
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                Tile t = grid.getTileAt(x, y);
                if (t != null)
                    parent.put(t, t);
            }
        }
        return parent;
    }

    private void unionPushEdges(Map<Tile, Tile> parent) {
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                Tile t = grid.getTileAt(x, y);
                if (t == null)
                    continue;
                for (Tile target : t.getPushTargets()) {
                    if (target != null)
                        union(parent, t, target);
                }
            }
        }
    }

    private List<List<Tile>> groupByRoot(Map<Tile, Tile> parent) {
        Map<Tile, List<Tile>> byRoot = new LinkedHashMap<>();
        for (int y = grid.getHeight() - 1; y >= 0; y--) {
            for (int x = grid.getWidth() - 1; x >= 0; x--) {
                Tile t = grid.getTileAt(x, y);
                if (t instanceof Updatable) {
                    Tile root = find(parent, t);
                    byRoot.computeIfAbsent(root, r -> new ArrayList<>()).add(t);
                }
            }
        }
        return new ArrayList<>(byRoot.values());
    }

    private static Tile find(Map<Tile, Tile> parent, Tile t) {
        Tile root = t;
        while (parent.get(root) != root) {
            root = parent.get(root);
        }
        while (parent.get(t) != root) {
            Tile next = parent.get(t);
            parent.put(t, root);
            t = next;
        }
        return root;
    }

    private static void union(Map<Tile, Tile> parent, Tile a, Tile b) {
        Tile ra = find(parent, a);
        Tile rb = find(parent, b);
        if (ra != rb)
            parent.put(ra, rb);
    }
}
