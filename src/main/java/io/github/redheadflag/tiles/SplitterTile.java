package io.github.redheadflag.tiles;

import java.util.Optional;

import io.github.redheadflag.world.Direction;
import io.github.redheadflag.world.Resource;
import io.github.redheadflag.world.Updatable;

public class SplitterTile extends Tile implements Updatable {
    private Direction direction;
    private Direction outputDirection;
    private int nextOutputIndex = 0; // 0 = right, 1 = left
    
    public SplitterTile(Direction direction) {
        super(TileType.SPLITTER, 1);
        this.direction = direction;
        this.outputDirection = direction.getOppositeDirection();
    }

    @Override
    public void tick(long tickCount) {
        if (!canProvide())
            return;

        // Peek the first resource so we don't lose it if transfer fails
        Optional<Resource> maybeItem = inventory.removeFirst();
        if (maybeItem.isEmpty())
            return;

        Resource item = maybeItem.get();

        for (int i = 0; i < 2; i++) {

            int idx = (nextOutputIndex + i) % 2;
            Tile outputTile = getOutputTile();

            if (outputTile == null)
                continue;

            if (!outputTile.canAccept()) 
                continue;

            outputTile.getInventory().add(item);
            
            nextOutputIndex = (idx + 1) % 2;
            return;
        }

        // If both outputs blocked → put item back
        inventory.add(item);
    }

    private Tile getOutputTile() {
        Tile outputTile;

        if (nextOutputIndex == 0) {
            outputTile = getNeighbourTile(outputDirection);
        }
        else {
            Direction left = switch (outputDirection) {
                case UP    -> Direction.LEFT;
                case LEFT  -> Direction.DOWN;
                case DOWN  -> Direction.RIGHT;
                case RIGHT -> Direction.UP;
            };

            outputTile = getNeighbourTile(left);
        }

        return outputTile;
    }

    @Override
    public String toString() {
        return getType().getDescription() + " (" + direction + ")";
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
