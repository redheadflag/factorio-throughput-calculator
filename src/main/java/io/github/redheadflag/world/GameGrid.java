package io.github.redheadflag.world;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import io.github.redheadflag.tiles.Tile;
import io.github.redheadflag.tiles.TileFactory;

public class GameGrid {
    private Tile[][] grid;
    private static final boolean strictGridSizeCheck = true;

    public GameGrid(int x, int y, Tile[][] grid) {
        this.grid = grid;
    }

    public Tile getTile(int x, int y) {
        if (grid == null) {
            return null;
        }
        if (x < 0 || y < 0 || x >= grid.length || y >= grid[x].length) {
            return null;
        }
        return grid[x][y];
    }

    public void tick(long tickCount) {
        if (grid == null) return;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                Tile t = grid[i][j];
                if (t instanceof Updatable u) {
                    u.tick(tickCount);
                }
            }
        }
    }

    public static GameGrid fromFile(String fp) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(fp));
            
            if (lines.isEmpty()) {
                throw new RuntimeException("File is empty: " + fp);
            }

            GridDimension dim = parseGridDimension(lines.get(0));

            List<String> gridList = lines.subList(1, lines.size());

            if (gridList.size() < dim.x() || strictGridSizeCheck && gridList.size() != dim.x()) {
                throw new RuntimeException("Expected " + dim.x() + " rows but found " + gridList.size());    
            }

            int x_size = dim.x(), y_size = dim.y();

            Tile[][] grid = new Tile[x_size][y_size];
            GameGrid gameGrid = new GameGrid(x_size, y_size, grid);
            
            for (int i = 0; i < grid.length; i++) {
                String[] tiles = gridList.get(i).split(" ");
                
                if (tiles.length < dim.y() || strictGridSizeCheck && tiles.length != dim.y()) {
                    throw new RuntimeException("Expected " + dim.y() + " columns but found " + tiles.length + " at row " + (i+1));
                }

                for (int j = 0; j < tiles.length; j++) {
                    String tileString = tiles[j].strip();
                    Tile tile = TileFactory.parseTile(tileString);
                    grid[i][j] = tile;
                    tile.attachToGrid(gameGrid, i, j);
                }
            }

            return gameGrid;
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + fp, e);
        }
    }

    private static GridDimension parseGridDimension(String line) {
        String[] parts = line.split(" ");
        if (parts.length != 2) {
            throw new RuntimeException("Grid dimension was not provided correctly. Example: 7 6");
        }
        return new GridDimension(
            Integer.parseInt(parts[0]),
            Integer.parseInt(parts[1])
        );
    }

    public void display() {
        System.out.println(this);
    }

    @Override
    public String toString() {
        if (grid == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < grid.length; i++) {
            Tile[] row = grid[i];
            for (int j = 0; j < row.length; j++) {
                Tile t = row[j];
                sb.append(t == null ? "null" : t.toString());
                if (j < row.length - 1) sb.append(' ');
            }
            if (i < grid.length - 1) sb.append(System.lineSeparator());
        }
        return sb.toString();
    }
}
