package io.github.redheadflag.tiles;

import io.github.redheadflag.world.Direction;
import io.github.redheadflag.world.Policies;
import io.github.redheadflag.world.TickContext;
import io.github.redheadflag.world.TransferService;
import io.github.redheadflag.world.Updatable;

public class ConveyorBeltTile extends Tile implements Updatable {
    private Direction direction;
    private final TransferService transfer = new TransferService();
    
    ConveyorBeltTile(Direction direction) {
        super(TileType.CONVEYOR_BELT, Policies.belt());
        this.direction = direction;
    }

    @Override
    public String toString() {
        return getType().getDescription() + " (" + direction + ")" + " (" + getX() + ", " + getY() + ")";
    }

    @Override
    public void tick(TickContext tickContext) {
        Tile target = getNeighbourTile(direction);
        if (target == null) return;

        boolean isTransfered = transfer.transferOne(this.inventory, target.getInventory(), tickContext.tickCount());
        if (isTransfered) {
            tickContext.logUpdate();  // TODO: Move to transferOne method
        }
    }
}
