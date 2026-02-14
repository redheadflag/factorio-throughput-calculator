package io.github.redheadflag.tiles;

import java.util.Optional;

import io.github.redheadflag.world.Direction;
import io.github.redheadflag.world.Resource;
import io.github.redheadflag.world.ResourceType;
import io.github.redheadflag.world.Updatable;

public class AssemblingStation2Tile extends Tile implements Updatable {
    private int processingTicksLeft = 0;
    private static final Direction OUTPUT = Direction.RIGHT;

    public AssemblingStation2Tile() {
        super(TileType.ASSEMBLING_STATION_2, 2);
    }

    @Override
    public void tick(long tickCount) {

        // 1) Try to push completed items out
        tryPushOutput();

        // 2) If already processing → count down
        if (processingTicksLeft > 0) {
            processingTicksLeft--;
            if (processingTicksLeft == 0) {
                // Finish recipe
                inventory.removeFirst(ResourceType.IRON);
                inventory.removeFirst(ResourceType.COPPER_WIRE);

                inventory.add(ResourceType.INDUCTOR);
            }
            return;
        }

        // 3) If idle → check recipe
        boolean hasIron = inventory.has(ResourceType.IRON);
        boolean hasWire = inventory.has(ResourceType.COPPER_WIRE);

        if (hasIron && hasWire) {
            processingTicksLeft = 3; // processing duration
        }
    }

    private void tryPushOutput() {
        if (inventory.isEmpty())
            return;

        Tile out = getNeighbourTile(OUTPUT);
        if (out == null)
            return;

        if (!out.canAccept())
            return;

        Optional<Resource> item = inventory.removeFirst();
        if (item.isEmpty())
            return;

        boolean ok = out.getInventory().add(item.get());
        if (!ok) {
            inventory.add(item.get());
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

    @Override
    public String toString() {
        return getType().getDescription();
    }
}
