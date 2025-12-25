package io.github.redheadflag.tiles;

import io.github.redheadflag.world.Direction;
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
        // TODO: implement movement logic: move resources from this tile to adjacent tile
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
