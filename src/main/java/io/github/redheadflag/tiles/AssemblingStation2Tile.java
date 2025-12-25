package io.github.redheadflag.tiles;

public class AssemblingStation2Tile extends Tile {
    public AssemblingStation2Tile() {
        super(TileType.ASSEMBLING_STATION_2, 2);
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
