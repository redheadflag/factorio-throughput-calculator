package io.github.redheadflag.tiles;

import java.util.Set;

import io.github.redheadflag.world.Direction;
import io.github.redheadflag.world.Policies;
import io.github.redheadflag.world.Resource;
import io.github.redheadflag.world.ResourceType;
import io.github.redheadflag.world.Updatable;

public class AssemblingStation2Tile extends Tile implements Updatable {
    private static final Direction OUTPUT = Direction.RIGHT;
    private static final int PROCESS_TIME_TICKS = 3;

    private int processingTicksLeft = 0;

    public AssemblingStation2Tile() {
        super(TileType.ASSEMBLING_STATION_2, Policies.only(Set.of(ResourceType.COPPER_WIRE, ResourceType.IRON), Integer.MAX_VALUE));
    }

    @Override
    public void tick(long tickCount) {
        tryPushOutput(tickCount);

        if (processingTicksLeft > 0) {
            processingTicksLeft--;
            if (processingTicksLeft == 0) {
                consumeIronAndWire();
                if (!produceInductorToNeighbor(tickCount)) {
                    inventory.add(ResourceType.IRON);
                    inventory.add(ResourceType.COPPER_WIRE);
                }
            }
            return;
        }

        if (inventory.has(ResourceType.IRON) && inventory.has(ResourceType.COPPER_WIRE)) {
            Tile out = getNeighbourTile(OUTPUT);
            if (out != null && out.getInventory() != null) {
                processingTicksLeft = PROCESS_TIME_TICKS;
            }
        }
    }

    private void tryPushOutput(long tickCount) {
        var peek = inventory.peekFirst();
        if (peek.isEmpty()) return;
        if (peek.get().type != ResourceType.INDUCTOR) return;

        Tile out = getNeighbourTile(OUTPUT);
        if (out == null) return;

        var outInv = out.getInventory();
        if (outInv == null) return;

        if (peek.get().movedThisTick(tickCount)) return;
        if (!outInv.canAdd(peek.get())) return;

        var removed = inventory.removeFirst();
        if (removed.isEmpty()) return;

        boolean ok = outInv.add(removed.get());
        if (!ok) {
            inventory.add(removed.get());
            return;
        }

        removed.get().markMoved(tickCount);
    }

    private void consumeIronAndWire() {
        inventory.removeFirst(ResourceType.IRON);
        inventory.removeFirst(ResourceType.COPPER_WIRE);
    }

    private boolean produceInductorToNeighbor(long tickCount) {
        Tile out = getNeighbourTile(OUTPUT);
        if (out == null) return false;

        var outInv = out.getInventory();
        if (outInv == null) return false;

        Resource inductor = new Resource(ResourceType.INDUCTOR);
        if (!outInv.canAdd(inductor)) return false;

        boolean ok = outInv.add(inductor);
        if (!ok) return false;

        inductor.markMoved(tickCount);
        return true;
    }

    @Override
    public String toString() {
        return getType().getDescription();
    }
}
