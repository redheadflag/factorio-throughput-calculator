package io.github.redheadflag.tiles;

import io.github.redheadflag.world.Direction;
import io.github.redheadflag.world.Policies;
import io.github.redheadflag.world.TransferService;
import io.github.redheadflag.world.Updatable;

public class SplitterTile extends Tile implements Updatable {

    private final Direction facing;
    private int nextOutputIndex = 0; // 0 = forward, 1 = forward-left
    private final TransferService transfer = new TransferService();

    public SplitterTile(Direction inputDirection) {
        super(TileType.SPLITTER, Policies.belt());
        this.facing = inputDirection.getOppositeDirection();
    }

    @Override
    public void tick(long tickCount) {
        if (inventory.isEmpty()) return;
        
        Tile out = (nextOutputIndex % 2 == 0) ? getForwardTile() : getForwardLeftTile();
        transfer.transferOne(this.inventory, out.getInventory(), tickCount);
        
        this.nextOutputIndex = (nextOutputIndex + 1) % 2;
    }

    private Tile getForwardTile() {
        int nx = getX() + facing.getDx();
        int ny = getY() + facing.getDy();

        return getGameGrid().getTileAt(nx, ny);
    }

    private Tile getForwardLeftTile() {
        int dx = facing.getDx();
        int dy = facing.getDy();

        int leftDx = +dy;
        int leftDy = -dx;

        int nx = getX() + dx + leftDx;
        int ny = getY() + dy + leftDy;

        return getGameGrid().getTileAt(nx, ny);
    }

    @Override
    public String toString() {
        return getType().getDescription() + " (" + facing + ")";
    }
}
