package io.github.redheadflag.world;

public class ChestTile extends Tile {
    public ChestTile() {
        super(TileType.CHEST);
    }

    @Override
    public String toString() {
        return getType().getDescription();
    }
}
