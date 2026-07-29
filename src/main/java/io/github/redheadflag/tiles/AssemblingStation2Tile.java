package io.github.redheadflag.tiles;

import java.util.Set;

import io.github.redheadflag.world.Policies;
import io.github.redheadflag.world.ResourceType;

public class AssemblingStation2Tile extends AssemblingStationTile {
    private static final int PROCESS_TIME_TICKS = 3;

    public AssemblingStation2Tile() {
        super(
            TileType.ASSEMBLING_STATION_2,
            Policies.only(Set.of(ResourceType.COPPER_WIRE, ResourceType.IRON), Integer.MAX_VALUE),
            PROCESS_TIME_TICKS,
            ResourceType.INDUCTOR
        );
    }

    @Override
    protected boolean hasRequiredInputs() {
        return inventory.has(ResourceType.IRON) && inventory.has(ResourceType.COPPER_WIRE);
    }

    @Override
    protected void consumeInputs() {
        inventory.removeFirstOfType(ResourceType.IRON);
        inventory.removeFirstOfType(ResourceType.COPPER_WIRE);
    }

    @Override
    protected void refundInputs() {
        inventory.add(ResourceType.IRON);
        inventory.add(ResourceType.COPPER_WIRE);
    }
}
