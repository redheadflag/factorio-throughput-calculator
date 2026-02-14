package io.github.redheadflag.tiles;

import java.util.Optional;

import io.github.redheadflag.world.Direction;
import io.github.redheadflag.world.Resource;
import io.github.redheadflag.world.Updatable;

public class ConveyorBeltTile extends Tile implements Updatable {
    private Direction direction;
    
    ConveyorBeltTile(Direction direction) {
        super(TileType.CONVEYOR_BELT, 1);
        this.direction = direction;
    }

    @Override
    public String toString() {
        return getType().getDescription() + " (" + direction + ")";
    }

    @Override
    public void tick(long tickCount) {
        if (!canProvide())
            return;

        Tile target = getNeighbourTile(direction);
        if (target == null)
            return;

        if (!target.canAccept())
            return;

        Optional<Resource> maybeItem = inventory.removeFirst();
        if (maybeItem.isEmpty())
            return;

        Resource item = maybeItem.get();

        boolean accepted = target.getInventory().add(item);

        if (!accepted) {
            inventory.add(item);
        }
    }

    @Override
    public boolean canAccept() {
        return !inventory.isFull();
    }

    @Override
    public boolean canProvide() {
        return !inventory.isEmpty();
    }
}
