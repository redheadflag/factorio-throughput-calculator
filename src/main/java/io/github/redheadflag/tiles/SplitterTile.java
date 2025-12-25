package io.github.redheadflag.tiles;

import io.github.redheadflag.world.Direction;

public class SplitterTile extends Tile {
    private Direction direction;
    
    public SplitterTile(Direction direction) {
        super(TileType.SPLITTER, 1);
        this.direction = direction;
    }

    @Override
    public String toString() {
        return getType().getDescription() + " (" + direction + ")";
    }

    @Override
    public boolean canAccept() {
        return !inventory.isFull();
    }

    @Override
    public boolean canProvide() {
        return !inventory.isEmpty();
    }
}
