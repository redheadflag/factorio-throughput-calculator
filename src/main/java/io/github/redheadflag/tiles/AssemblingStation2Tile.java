package io.github.redheadflag.tiles;

import java.util.Optional;
import java.util.Set;

import io.github.redheadflag.world.Direction;
import io.github.redheadflag.world.Policies;
import io.github.redheadflag.world.Resource;
import io.github.redheadflag.world.ResourceType;
import io.github.redheadflag.world.TickContext;
import io.github.redheadflag.world.TransferService;
import io.github.redheadflag.world.Updatable;

public class AssemblingStation2Tile extends Tile implements Updatable {
    private static final Direction OUTPUT = Direction.RIGHT;
    private static final int PROCESS_TIME_TICKS = 3;

    private final TransferService transfer = new TransferService();

    private int processingTicksLeft = 0;

    public AssemblingStation2Tile() {
        super(TileType.ASSEMBLING_STATION_2, Policies.only(Set.of(ResourceType.COPPER_WIRE, ResourceType.IRON), Integer.MAX_VALUE));
    }

    @Override
    public void tick(TickContext tickContext) {
        tryPushOutput(tickContext);

        if (processingTicksLeft > 0) {
            processingTicksLeft--;
            if (processingTicksLeft == 0) {
                consumeIronAndWire();
                if (!produceInductorToNeighbor(tickContext.tickCount())) {
                    inventory.add(ResourceType.IRON);
                    inventory.add(ResourceType.COPPER_WIRE);
                }
                else {
                    tickContext.logUpdate();
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

    private void tryPushOutput(TickContext tickContext) {
        Optional<Resource> peek = inventory.peekFirst();
        if (peek.isEmpty()) return;

        if (peek.get().type != ResourceType.COPPER_WIRE) return;

        Tile out = getNeighbourTile(OUTPUT);
        if (out == null) return;

        transfer.transferOne(this, out, tickContext);
    }

    private void consumeIronAndWire() {
        inventory.removeFirstOfType(ResourceType.IRON);
        inventory.removeFirstOfType(ResourceType.COPPER_WIRE);
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
