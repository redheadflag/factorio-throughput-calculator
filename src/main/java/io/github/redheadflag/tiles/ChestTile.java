package io.github.redheadflag.tiles;

import io.github.redheadflag.world.Policies;

public class ChestTile extends Tile {
    public ChestTile() {
        super(TileType.CHEST, Policies.chestInfinite());
    }

    @Override
    public String toString() {
        return getType().getDescription();
    }
}
