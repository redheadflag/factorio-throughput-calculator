package io.github.redheadflag.tiles;

import io.github.redheadflag.world.Inventory;
import io.github.redheadflag.world.Resource;
import io.github.redheadflag.world.ResourceType;

public interface StoragePolicy {
    boolean canInsert(Inventory inv, Resource res);
    boolean canExtract(Inventory inv, ResourceType type);
    int maxSlots();
}
