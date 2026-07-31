package io.github.redheadflag.world;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.github.redheadflag.tiles.Tile;
import io.github.redheadflag.tiles.TileFactory;

public class GameGrid {

    // grid[y][x]
    private final Tile[][] grid;
    private final int width;
    private final int height;

    private static final boolean strictGridSizeCheck = true;

    private List<List<Tile>> components;
    private List<Callable<Boolean>> tasks;
    private ExecutorService pool;

    private TickContext currentTickContext;

    public GameGrid(int width, int height, Tile[][] grid) {
        this.width = width;
        this.height = height;
        this.grid = grid;
    }

    /* ========================
       Accessors
       ======================== */

    public Tile getTileAt(int x, int y) {
        if (!isInBounds(x, y)) {
            return null;
        }
        return grid[y][x];
    }

    public boolean isInBounds(int x, int y) {
        return grid != null
            && x >= 0 && y >= 0
            && x < width
            && y < height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /* ========================
       Tick
       ======================== */

    public void tick(TickContext tickContext) {
        currentTickContext = tickContext;

        if (components == null) {
            components = new TileComponents(this).compute();
        }
        if (tasks == null) {
            tasks = new ArrayList<>();
            for (List<Tile> component : components) {
                tasks.add(() -> tickComponent(component, currentTickContext));
            }
        }
        if (pool == null) {
            int threads = Runtime.getRuntime().availableProcessors() - 1;
            pool = Executors.newFixedThreadPool(threads);
        }

        try {
            List<Future<Boolean>> futures = pool.invokeAll(tasks);

            boolean anyUpdate = false;
            for (Future<Boolean> future : futures) {
                if (future.get()) {
                    anyUpdate = true;
                }
            }
            if (anyUpdate) {
                tickContext.logUpdate();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while ticking grid", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("A tile failed to tick", e.getCause());
        }
    }

    private boolean tickComponent(List<Tile> component, TickContext tickContext) {
        boolean anyUpdate = false;
        for (Tile t : component) {
            if (((Updatable) t).tick(tickContext)) {
                anyUpdate = true;
            }
        }
        return anyUpdate;
    }

    public void shutdown() {
        if (pool != null) {
            pool.shutdownNow();
            pool = null;
        }
    }

    /* ========================
       Loading
       ======================== */

    public static GameGrid fromFile(String fp) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(fp));

            if (lines.isEmpty()) {
                throw new RuntimeException("File is empty: " + fp);
            }

            GridDimension dim = parseGridDimension(lines.get(0));
            int width = dim.x();
            int height = dim.y();

            List<String> rows = lines.subList(1, lines.size());

            if (rows.size() < height || (strictGridSizeCheck && rows.size() != height)) {
                throw new RuntimeException(
                    "Expected " + height + " rows but found " + rows.size()
                );
            }

            Tile[][] grid = new Tile[height][width];
            GameGrid gameGrid = new GameGrid(width, height, grid);

            for (int y = 0; y < height; y++) {
                String[] tiles = rows.get(y).trim().split("\\s+");

                if (tiles.length < width || (strictGridSizeCheck && tiles.length != width)) {
                    throw new RuntimeException(
                        "Expected " + width + " columns but found " + tiles.length + " at row " + (y + 1)
                    );
                }

                for (int x = 0; x < width; x++) {
                    Tile tile = TileFactory.parseTile(tiles[x].strip());
                    grid[y][x] = tile;
                    tile.attachToGrid(gameGrid, x, y);
                }
            }

            return gameGrid;

        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + fp, e);
        }
    }

    private static GridDimension parseGridDimension(String line) {
        String[] parts = line.split(" ");
        if (parts.length != 2) {
            throw new RuntimeException("Grid dimension was not provided correctly. Example: 7 6");
        }
        return new GridDimension(
            Integer.parseInt(parts[1]), // width (x)
            Integer.parseInt(parts[0])  // height (y)
        );
    }

    /* ========================
       Display
       ======================== */

    public void display() {
        System.out.println(this);
    }

    @Override
    public String toString() {
        if (grid == null) return "";

        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile t = grid[y][x];
                sb.append(t == null ? "null" : t.toString());
                if (x < width - 1) sb.append(' ');
            }
            if (y < height - 1) sb.append(System.lineSeparator());
        }
        return sb.toString();
    }
}
