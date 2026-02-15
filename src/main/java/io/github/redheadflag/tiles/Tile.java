package io.github.redheadflag.tiles;

import java.util.ArrayList;
import java.util.List;

import io.github.redheadflag.world.Direction;
import io.github.redheadflag.world.GameGrid;
import io.github.redheadflag.world.Inventory;

public abstract class Tile {
    private TileType type;
    protected final Inventory inventory;

    private int x = -1;
    private int y = -1;
    private GameGrid gameGrid;

    public Tile(TileType type, StoragePolicy policy) {
        this.type = type;
        this.inventory = new Inventory(policy);
    }

    public TileType getType() {
        return type;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void attachToGrid(GameGrid gameGrid, int x, int y) {
        this.gameGrid = gameGrid;
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public GameGrid getGameGrid() { return gameGrid; }

    public Tile getNeighbourTile(Direction direction) {
        if (gameGrid == null) throw new IllegalStateException("Tile not attached to a GameGrid");
        int nx = x + direction.getDx();
        int ny = y + direction.getDy();
        return gameGrid.getTileAt(nx, ny);
    }

    public List<Tile> getNeighbours() {
        if (gameGrid == null) throw new IllegalStateException("Tile not attached to a GameGrid");
        List<Tile> neighbours = new ArrayList<>();
        for (Direction d : Direction.values()) {
            Tile n = getNeighbourTile(d);
            if (n != null) neighbours.add(n);
        }
        return neighbours;
    }

    @Override
    public String toString() {
        return type.getDescription();
    }
}
