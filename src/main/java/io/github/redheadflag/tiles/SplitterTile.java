package io.github.redheadflag.tiles;

import io.github.redheadflag.world.Direction;
import io.github.redheadflag.world.Policies;
import io.github.redheadflag.world.TransferService;
import io.github.redheadflag.world.Updatable;

public class SplitterTile extends Tile implements Updatable {

    private final Direction facing;
    private int nextOutputIndex = 0; // 0 = left, 1 = right
    private final TransferService transfer = new TransferService();

    public SplitterTile(Direction inputDirection) {
        super(TileType.SPLITTER, Policies.belt());
        this.facing = inputDirection.getOppositeDirection();
    }

    @Override
    public void tick(long tickCount) {
        if (inventory.isEmpty()) return;
        
        Tile out = getSideTile(nextOutputIndex % 2 == 0);
        transfer.transferOne(this.inventory, out.getInventory(), tickCount);
        
        this.nextOutputIndex = (nextOutputIndex + 1) % 2;
    }

    private Tile getSideTile(boolean left) {
        int dx = facing.getDx();
        int dy = facing.getDy();

        int sideDx = left ? +dy : -dy;
        int sideDy = left ? -dx : +dx;

        int nx = getX() + sideDx;
        int ny = getY() + sideDy;

        return getGameGrid().getTileAt(nx, ny);
    }

    @Override
    public String toString() {
        return getType().getDescription() + " (" + facing + ")";
    }
}
