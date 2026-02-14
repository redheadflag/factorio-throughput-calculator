package io.github.redheadflag.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Inventory {
private final List<ResourceSlot> slots = new ArrayList<>();
    private final boolean infinite;

    public Inventory(int slotCount) {
        this.infinite = (slotCount < 0);
        if (!infinite) {
            for (int i = 0; i < slotCount; i++) {
                slots.add(new ResourceSlot());
            }
        }
    }

    public int size() {
        return infinite ? slots.size() : slots.size();
    }

    public List<ResourceSlot> getSlots() {
        return Collections.unmodifiableList(slots);
    }

    public boolean add(ResourceType resourceType) {
        Resource res = new Resource(resourceType);
        return add(res);
    }

    public boolean add(Resource res) {
        // Try empty slots first
        for (ResourceSlot slot : slots) {
            if (slot.isEmpty()) return slot.put(res);
        }

        // If chest → infinite slots
        if (infinite) {
            ResourceSlot slot = new ResourceSlot();
            slot.put(res);
            slots.add(slot);
            return true;
        }

        return false; // no room
    }

    public Optional<Resource> removeFirst(ResourceType type) {
        for (ResourceSlot slot : slots) {
            if (!slot.isEmpty() && slot.get().type == type) {
                return Optional.of(slot.remove());
            }
        }
        return Optional.empty();
    }

    public Optional<Resource> removeFirst() {
        for (ResourceSlot slot : slots) {
            if (!slot.isEmpty()) {
                return Optional.of(slot.remove());
            }
        }
        return Optional.empty();
    }

    public boolean has(ResourceType type) {
        return slots.stream()
                .anyMatch(s -> !s.isEmpty() && s.get().type == type);
    }

    /** Returns true if all slots are full (infinite slots are never full) */
    public boolean isFull() {
        if (infinite) return false;
        return slots.stream().allMatch(slot -> !slot.isEmpty());
    }

    /** Returns true if all slots are empty */
    public boolean isEmpty() {
        return slots.stream().allMatch(ResourceSlot::isEmpty);
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
