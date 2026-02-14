package io.github.redheadflag.tiles;

import java.util.Optional;

import io.github.redheadflag.world.Direction;
import io.github.redheadflag.world.Resource;
import io.github.redheadflag.world.ResourceType;
import io.github.redheadflag.world.Updatable;

public class AssemblingStation1Tile extends Tile implements Updatable {
    private int processingTicksLeft = 0;
    private static final Direction OUTPUT = Direction.RIGHT;

    public AssemblingStation1Tile() {
        super(TileType.ASSEMBLING_STATION_1, 2);
    }

    @Override
    public void tick(long tickCount) {

        // 1) Try to push any finished product out first
        tryPushOutput();

        // 2) If currently processing, count down
        if (processingTicksLeft > 0) {
            processingTicksLeft--;
            if (processingTicksLeft == 0) {
                // Recipe completes now
                inventory.removeFirst(ResourceType.COPPER);
                inventory.removeFirst(ResourceType.COPPER);

                inventory.add(ResourceType.COPPER_WIRE);
            }
            return;
        }

        // 3) Not processing → check if we can start a recipe
        boolean hasTwoCopper =
                inventory.has(ResourceType.COPPER) &&
                inventory.getSlots().stream()
                        .filter(s -> !s.isEmpty() && s.get().type == ResourceType.COPPER)
                        .count() >= 2;

        if (hasTwoCopper) {
            processingTicksLeft = 2;   // processing duration
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
            // restore if somehow failed
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
