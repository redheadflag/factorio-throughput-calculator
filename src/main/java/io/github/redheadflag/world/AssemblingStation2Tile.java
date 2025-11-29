package io.github.redheadflag.world;

public class AssemblingStation2Tile extends Tile {
    public AssemblingStation2Tile() {
        super(TileType.ASSEMBLING_STATION_2);
    }

    @Override
    public String toString() {
        return getType().getDescription();
    }
}
