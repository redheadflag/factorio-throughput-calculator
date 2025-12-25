package io.github.redheadflag.tiles;

public class EmptyTile extends Tile {
    public EmptyTile() {
        super(TileType.EMPTY, 0);
    }

    @Override
    public String toString() {
        return getType().getDescription();
    }

    @Override
    public boolean canAccept() {
        return false;
    }

    @Override
    public boolean canProvide() {
        return false;
    }
}
