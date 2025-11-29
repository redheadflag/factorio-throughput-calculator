package io.github.redheadflag.world;

public class AssemblingStation1Tile extends Tile {
    public AssemblingStation1Tile() {
        super(TileType.ASSEMBLING_STATION_1);
    }

    @Override
    public String toString() {
        return getType().getDescription();
    }
}
