package io.github.redheadflag.tiles;

import java.util.List;

import io.github.redheadflag.world.Policies;
import io.github.redheadflag.world.ResourceType;
import io.github.redheadflag.world.TickContext;
import io.github.redheadflag.world.TransferService;
import io.github.redheadflag.world.Updatable;

public class SourceTile extends Tile implements Updatable {
    private final ResourceType resourceType;
    private final TransferService transfer = new TransferService();

    public SourceTile(ResourceType resourceType, int capacity) {
        super(TileType.SOURCE, Policies.buffer(capacity));
        this.resourceType = resourceType;
        this.inventory.fill(resourceType);
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    @Override
    public boolean tick(TickContext tickContext) {
        List<Tile> neighbours = getNeighbours();
        boolean moved = false;
        for (Tile neighbour : neighbours) {
            if (transfer.transferOne(this, neighbour, tickContext)) {
                moved = true;
            }
        }
        return moved;
    }

    @Override
    public List<Tile> getPushTargets() {
        return getNeighbours();
    }
}
