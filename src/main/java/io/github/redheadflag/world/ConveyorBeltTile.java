package io.github.redheadflag.world;

public class ConveyorBeltTile extends Tile {
    private Direction direction;
    
    ConveyorBeltTile(Direction direction) {
        super(TileType.CONVEYOR_BELT);
        this.direction = direction;
    }

    @Override
    public String toString() {
        return getType().getDescription() + " (" + direction + ")";
    }
}
