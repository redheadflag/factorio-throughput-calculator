package io.github.redheadflag.world;

public abstract class Tile {
    protected TileType type;

    public Tile(TileType type) {
        this.type = type;
    }

    public TileType getType() {
        return type;
    }

    @Override
    public String toString() {
        return type.getDescription();
    }
}
