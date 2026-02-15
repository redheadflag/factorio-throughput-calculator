package io.github.redheadflag.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.github.redheadflag.tiles.StoragePolicy;
import io.github.redheadflag.tiles.Tile;

public class Inventory {
    private final List<ResourceSlot> slots = new ArrayList<>();
    private final StoragePolicy policy;

    public Inventory(StoragePolicy policy) {
        this.policy = policy;

        int cap = policy.maxSlots();
        if (cap != Integer.MAX_VALUE) {
            for (int i = 0; i < cap; i++) slots.add(new ResourceSlot());
        }
    }

    public StoragePolicy getPolicy() {
        return policy;
    }

    /** Number of non-empty slots (i.e., number of items with your current 1-per-slot model). */
    public int itemCount() {
        int c = 0;
        for (ResourceSlot s : slots) if (!s.isEmpty()) c++;
        return c;
    }

    public int slotCount() {
        return slots.size();
    }

    public int capacity() {
        return policy.maxSlots();
    }

    public void fill(ResourceType resourceType) {
        while(!isFull()) {
            add(resourceType);
        }
    }

    public boolean canAdd(Resource res) {
        return policy.canInsert(this, res);
    }

    public boolean add(ResourceType resourceType) {
        return add(new Resource(resourceType));
    }

    public boolean add(Resource res) {
        if (!policy.canInsert(this, res)) return false;

        // Try empty slots first
        for (ResourceSlot slot : slots) {
            if (slot.isEmpty()) return slot.put(res);
        }

        // If infinite capacity, grow list
        if (policy.maxSlots() == Integer.MAX_VALUE) {
            slots.add(new ResourceSlot(res));
            return true;
        }

        return false;
    }

    public Optional<Resource> peekFirst() {
        for (ResourceSlot slot : slots) {
            if (!slot.isEmpty()) return Optional.of(slot.get());
        }
        return Optional.empty();
    }

    public Optional<Resource> removeFirst() {
        for (ResourceSlot slot : slots) {
            if (!slot.isEmpty()) return Optional.of(slot.remove());
        }
        return Optional.empty();
    }

    public Optional<Resource> removeFirst(ResourceType type) {
        for (ResourceSlot slot : slots) {
            if (!slot.isEmpty() && slot.get().type == type) return Optional.of(slot.remove());
        }

        return Optional.empty();
    }

    public boolean has(ResourceType type) {
        for (ResourceSlot s : slots) {
            if (!s.isEmpty() && s.get().type == type) return true;
        }
        return false;
    }

    public boolean isFull() {
        if (policy.maxSlots() == Integer.MAX_VALUE) return false;
        return itemCount() >= policy.maxSlots();
    }

    public boolean isEmpty() {
        return itemCount() == 0;
    }

    public List<ResourceSlot> getSlots() {
        return Collections.unmodifiableList(slots);
    }

    public boolean transferFirstTo(Inventory targetInv, long tick) {
        Optional<Resource> removed = this.removeFirst();
        if (removed.isEmpty()) return false;
        
        Resource res = removed.get();
        if (res.movedThisTick(tick)) {
            this.add(res);
            return false;
        }

        boolean added = targetInv.add(res);
        if (!added) {
            this.add(res);
            return false;
        }

        res.markMoved(tick);

        return true;
    }

    public boolean transferFirstTo(Tile targetTile, long tick) {
        Inventory targetInv = targetTile.getInventory();
        if (targetInv == null) return false;
        return transferFirstTo(targetInv, tick);
    }

    /**
     * Checks whether inventory has at least the required amount of each resource.
     * @param required Map of ResourceType -> quantity required
     */
    public boolean hasAll(Map<ResourceType, Integer> required) {
        Map<ResourceType, Long> counts = new EnumMap<>(ResourceType.class);

        // count resources in inventory
        for (ResourceSlot slot : slots) {
            if (!slot.isEmpty()) {
                ResourceType type = slot.get().type;
                counts.put(type, counts.getOrDefault(type, 0L) + 1);
            }
        }

        // check required amounts
        for (Map.Entry<ResourceType, Integer> entry : required.entrySet()) {
            long available = counts.getOrDefault(entry.getKey(), 0L);
            if (available < entry.getValue()) return false;
        }

        return true;
    }
}
