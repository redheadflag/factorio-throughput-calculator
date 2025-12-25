package io.github.redheadflag.tiles;

public class ChestTile extends Tile {
    public ChestTile() {
        super(TileType.CHEST, true);
    }

    @Override
    public String toString() {
        return getType().getDescription();
    }

    @Override
    public boolean canAccept() {
        return true;
    }

    @Override
    public boolean canProvide() {
        return false;
    }
}
