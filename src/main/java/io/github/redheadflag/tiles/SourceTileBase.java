package io.github.redheadflag.tiles;

import java.util.List;

import io.github.redheadflag.world.Policies;
import io.github.redheadflag.world.ResourceType;
import io.github.redheadflag.world.TransferService;
import io.github.redheadflag.world.Updatable;

public class SourceTileBase extends Tile implements Updatable {
    ResourceType resourceType;
    private final TransferService transfer = new TransferService();

    public SourceTileBase(ResourceType resourceType, int capacity) {
        super(TileType.SOURCE, Policies.buffer(capacity));
        this.resourceType = resourceType;
        this.inventory.fill(resourceType);
    }

    @Override
    public void tick(long tickCount) {
        List<Tile> neighbours = getNeighbours();
        for (Tile neighbour : neighbours) {
            transfer.transferOne(this.inventory, neighbour.inventory, tickCount);
            // isProvided = inventory.transferFirstTo(neighbour, tickCount);
        }
    }
}
