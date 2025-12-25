package io.github.redheadflag.tiles;

import java.util.List;

import io.github.redheadflag.world.ResourceFactory;
import io.github.redheadflag.world.ResourceType;
import io.github.redheadflag.world.Updatable;

public class SourceTileBase extends Tile implements Updatable {
    ResourceType resourceType;

    public SourceTileBase(ResourceType resourceType, int capacity) {
        super(TileType.SOURCE, capacity);
        this.resourceType = resourceType;
    }

    @Override
    public void tick(long tickCount) {
        inventory.add(ResourceFactory.createResource(resourceType, 1));

        List<Tile> neighbours = getNeighbours();
        for (Tile neighbour : neighbours) {
            if (!canProvide()) {
                return;
            }

            if (neighbour.canAccept()) {
                inventory.removeFirst(resourceType)
                    .ifPresent(neighbour.inventory::add);
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
