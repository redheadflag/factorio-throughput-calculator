package io.github.redheadflag.tiles;

import java.util.List;

import io.github.redheadflag.world.ResourceType;
import io.github.redheadflag.world.Updatable;

public class SourceTileBase extends Tile implements Updatable {
    ResourceType resourceType;

    public SourceTileBase(ResourceType resourceType, int capacity) {
        super(TileType.SOURCE, capacity);
        this.resourceType = resourceType;
        this.inventory.fill(resourceType);
    }

    @Override
    public void tick(long tickCount) {
        if (!canProvide()) return;

        boolean isProvided = false;

        List<Tile> neighbours = getNeighbours();
        for (Tile neighbour : neighbours) {
            if (isProvided) return;

            if (neighbour.canAccept()) {
                inventory.removeFirst(resourceType)
                    .ifPresent(neighbour.inventory::add);
                isProvided = true;
            }
        }
    }

    @Override
    public boolean canAccept() {
        return false;
    }

    @Override
    public boolean canProvide() {
        return !inventory.isEmpty();
    }
}
