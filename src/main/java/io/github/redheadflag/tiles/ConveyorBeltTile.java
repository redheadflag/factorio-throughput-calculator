package io.github.redheadflag.tiles;

import java.util.List;

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

    public Direction getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return getType().getDescription() + " (" + direction + ")" + " (" + getX() + ", " + getY() + ")";
    }

    @Override
    public boolean tick(TickContext tickContext) {
        Tile target = getNeighbourTile(direction);
        if (target == null) return false;

        return transfer.transferOne(this, target, tickContext);
    }

    @Override
    public List<Tile> getPushTargets() {
        Tile target = getNeighbourTile(direction);
        return target == null ? List.of() : List.of(target);
    }

    @Override
    public List<Tile> getForwardTargets() {
        return getPushTargets();
    }
}
