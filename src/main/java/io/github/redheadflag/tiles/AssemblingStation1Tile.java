package io.github.redheadflag.tiles;

import java.util.Map;
import java.util.Set;

import io.github.redheadflag.world.Policies;
import io.github.redheadflag.world.ResourceType;

public class AssemblingStation1Tile extends AssemblingStationTile {
    private static final int PROCESS_TIME_TICKS = 2;

    public AssemblingStation1Tile() {
        super(
            TileType.ASSEMBLING_STATION_1,
            Policies.only(Set.of(ResourceType.COPPER), 2),
            PROCESS_TIME_TICKS,
            ResourceType.COPPER_WIRE
        );
    }

    @Override
    protected boolean hasRequiredInputs() {
        return inventory.hasAll(Map.of(ResourceType.COPPER, 2));
    }

    @Override
    protected void consumeInputs() {
        inventory.removeFirstOfType(ResourceType.COPPER);
        inventory.removeFirstOfType(ResourceType.COPPER);
    }

    @Override
    protected void refundInputs() {
        inventory.add(ResourceType.COPPER);
        inventory.add(ResourceType.COPPER);
    }
}
