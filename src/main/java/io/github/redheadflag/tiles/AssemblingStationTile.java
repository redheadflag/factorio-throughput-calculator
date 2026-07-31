package io.github.redheadflag.tiles;

import java.util.List;
import java.util.Optional;

import io.github.redheadflag.world.Direction;
import io.github.redheadflag.world.Inventory;
import io.github.redheadflag.world.Resource;
import io.github.redheadflag.world.ResourceType;
import io.github.redheadflag.world.TickContext;
import io.github.redheadflag.world.TransferService;
import io.github.redheadflag.world.Updatable;

public abstract class AssemblingStationTile extends Tile implements Updatable {
    protected static final Direction OUTPUT = Direction.RIGHT;

    private final TransferService transfer = new TransferService();
    private final int processTimeTicks;
    private final ResourceType outputResourceType;
    private int processingTicksLeft = 0;

    protected AssemblingStationTile(
        TileType type,
        StoragePolicy policy,
        int processTimeTicks,
        ResourceType outputResourceType
    ) {
        super(type, policy);
        this.processTimeTicks = processTimeTicks;
        this.outputResourceType = outputResourceType;
    }

    @Override
    public boolean tick(TickContext tickContext) {
        boolean moved = tryPushOutput(tickContext);

        if (processingTicksLeft > 0) {
            --processingTicksLeft;
            if (processingTicksLeft == 0) {
                consumeInputs();

                if (!produceOutputToNeighbor(tickContext.tickCount())) {
                    refundInputs();
                } else {
                    moved = true;
                }
            }
            return moved;
        }

        if (hasRequiredInputs()) {
            Tile out = getOutputTile();
            if (out != null && out.getInventory() != null) {
                processingTicksLeft = processTimeTicks;
            }
        }

        return moved;
    }

    public Tile getOutputTile() {
        return getNeighbourTile(OUTPUT);
    }

    @Override
    public List<Tile> getPushTargets() {
        Tile out = getOutputTile();
        return out == null ? List.of() : List.of(out);
    }

    private boolean tryPushOutput(TickContext tickContext) {
        Optional<Resource> peek = inventory.peekFirst();
        if (peek.isEmpty()) return false;

        if (peek.get().type != outputResourceType) return false;

        Tile out = getOutputTile();
        if (out == null) return false;

        return transfer.transferOne(this, out, tickContext);
    }

    private boolean produceOutputToNeighbor(long tickCount) {
        Tile out = getOutputTile();
        if (out == null) return false;

        Inventory outInv = out.getInventory();
        if (outInv == null) return false;

        Resource produced = new Resource(outputResourceType);
        if (!outInv.canAdd(produced)) return false;

        boolean ok = outInv.add(produced);
        if (!ok) return false;

        produced.markMoved(tickCount);
        return true;
    }

    protected abstract boolean hasRequiredInputs();

    protected abstract void consumeInputs();

    protected abstract void refundInputs();

    @Override
    public String toString() {
        return getType().getDescription();
    }
}
