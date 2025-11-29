package io.github.redheadflag.world;

public class EmptyTile extends Tile {
    public EmptyTile() {
        super(TileType.EMPTY);
    }

    @Override
    public String toString() {
        return getType().getDescription();
    }
}
