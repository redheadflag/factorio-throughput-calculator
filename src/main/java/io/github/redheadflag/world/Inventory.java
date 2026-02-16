package io.github.redheadflag.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.github.redheadflag.tiles.StoragePolicy;
import io.github.redheadflag.tiles.Tile;

public class Inventory {
    private final List<Resource> items = new ArrayList<>();
    private final StoragePolicy policy;

    public Inventory(StoragePolicy policy) {
        this.policy = policy;
    }

    public StoragePolicy getPolicy() {
        return policy;
    }

    public int itemCount() {
        return items.size();
    }

    public int capacity() {
        return policy.maxItems();
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
        return items.add(res);
    }

    public Optional<Resource> peekFirst() {
        if (itemCount() == 0)
            return Optional.empty();

        return items.stream().findFirst();
    }

    public Optional<Resource> removeFirst() {
        if (itemCount() == 0)
            return Optional.empty();

        return Optional.of(items.remove(0));
    }

    public Optional<Resource> removeFirstOfType(ResourceType type) {
        for (int i = 0; i < itemCount(); i++) {
            Resource res = items.get(i);
            if (res.type == type) {
                return Optional.of(items.remove(i));
            }
        }
        return Optional.empty();
    }

    public boolean has(ResourceType type) {
        for (Resource res : items) {
            if (res.type == type) return true;
        }
        return false;
    }

    public boolean isFull() {
        return itemCount() >= policy.maxItems();
    }

    public boolean isEmpty() {
        return itemCount() == 0;
    }

    public List<Resource> getItems() {
        return Collections.unmodifiableList(items);
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
        for (Resource res : items) {
            counts.put(res.type, counts.getOrDefault(res.type, 0L) + 1);
        }

        // check required amounts
        for (Map.Entry<ResourceType, Integer> entry : required.entrySet()) {
            long available = counts.getOrDefault(entry.getKey(), 0L);
            if (available < entry.getValue()) return false;
        }

        return true;
    }

    public Map<ResourceType, Long> countByType() {
        return this
            .getItems()
            .stream()
            .collect(
                Collectors.groupingBy(
                    s -> s.type,
                    Collectors.counting()
                )
            );
    }
}
