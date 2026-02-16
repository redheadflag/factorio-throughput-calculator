package io.github.redheadflag.tiles;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import io.github.redheadflag.world.Direction;
import io.github.redheadflag.world.Policies;
import io.github.redheadflag.world.Resource;
import io.github.redheadflag.world.ResourceType;
import io.github.redheadflag.world.TickContext;
import io.github.redheadflag.world.TransferService;
import io.github.redheadflag.world.Updatable;

public class AssemblingStation1Tile extends Tile implements Updatable {
    private static final Direction OUTPUT = Direction.RIGHT;
    private static final int PROCESS_TIME_TICKS = 2;

    private final TransferService transfer = new TransferService();

    private int processingTicksLeft = 0;

    public AssemblingStation1Tile() {
        super(TileType.ASSEMBLING_STATION_1, Policies.only(Set.of(ResourceType.COPPER), 2));
    }

    @Override
    public void tick(TickContext tickContext) {
        tryPushOutput(tickContext.tickCount());

        if (processingTicksLeft > 0) {
            --processingTicksLeft;
            if (processingTicksLeft == 0) {
                consumeTwoCopper();

                if (!produceWireToNeighbor(tickContext.tickCount())) {
                    inventory.add(ResourceType.COPPER);
                    inventory.add(ResourceType.COPPER);
                }
                else {
                    tickContext.logUpdate();
                }
            }
            return;
        }

        if (inventory.hasAll(Map.of(
            ResourceType.COPPER, 2
        ))) {
            Tile out = getNeighbourTile(OUTPUT);
            if (out != null && out.getInventory() != null) {
                processingTicksLeft = PROCESS_TIME_TICKS;
            }
        }
    }

    private void tryPushOutput(long tickCount) {
        Optional<Resource> peek = inventory.peekFirst();
        if (peek.isEmpty()) return;

        if (peek.get().type != ResourceType.COPPER_WIRE) return;

        Tile out = getNeighbourTile(OUTPUT);
        if (out == null) return;

        transfer.transferOne(inventory, out.getInventory(), tickCount);
    }

    private void consumeTwoCopper() {
        inventory.removeFirstOfType(ResourceType.COPPER);
        inventory.removeFirstOfType(ResourceType.COPPER);
    }

    private boolean produceWireToNeighbor(long tickCount) {
        Tile out = getNeighbourTile(OUTPUT);
        if (out == null) return false;

        Resource wire = new Resource(ResourceType.COPPER_WIRE);

        if (!out.getInventory().canAdd(wire)) return false;

        boolean ok = out.getInventory().add(wire);
        if (!ok) return false;

        wire.markMoved(tickCount);
        return true;
    }

    @Override
    public String toString() {
        return getType().getDescription();
    }
}
