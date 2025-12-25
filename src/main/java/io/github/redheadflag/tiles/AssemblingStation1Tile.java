package io.github.redheadflag.tiles;

public class AssemblingStation1Tile extends Tile {
    public AssemblingStation1Tile() {
        super(TileType.ASSEMBLING_STATION_1, 2);
    }

    @Override
    public String toString() {
        return getType().getDescription();
    }

    @Override
    public boolean canAccept() {
        return !inventory.isFull();
    }

    @Override
    public boolean canProvide() {
        return false;
    }
}
