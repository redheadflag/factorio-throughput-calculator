package io.github.redheadflag.world;

public class SplitterTile extends Tile {
    private Direction direction;
    
    public SplitterTile(Direction direction) {
        super(TileType.SPLITTER);
        this.direction = direction;
    }

    @Override
    public String toString() {
        return getType().getDescription() + " (" + direction + ")";
    }
}
