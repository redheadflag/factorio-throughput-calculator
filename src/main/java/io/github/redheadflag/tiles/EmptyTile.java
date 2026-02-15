package io.github.redheadflag.tiles;

import io.github.redheadflag.world.Policies;

public class EmptyTile extends Tile {
    public EmptyTile() {
        super(TileType.EMPTY, Policies.empty());
    }

    @Override
    public String toString() {
        return getType().getDescription();
    }
}
