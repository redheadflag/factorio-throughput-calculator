package io.github.redheadflag.world;

import java.util.Set;

import io.github.redheadflag.tiles.StoragePolicy;

public class Policies {

    private Policies() {}

    public static StoragePolicy empty() {
        return new BasicPolicy(0, null);
    }

    public static StoragePolicy belt() {
        return new BasicPolicy(1, null);
    }

    public static StoragePolicy buffer(int slots) {
        return new BasicPolicy(slots, null);
    }

    public static StoragePolicy chestInfinite() {
        return new BasicPolicy(Integer.MAX_VALUE, null);
    }

    public static StoragePolicy only(Set<ResourceType> allowed, int maxSlots) {
        return new BasicPolicy(maxSlots, allowed);
    }

    private static class BasicPolicy implements StoragePolicy {
        private final int maxItems;
        private final Set<ResourceType> allowed;

        private BasicPolicy(int maxItems, Set<ResourceType> allowed) {
            this.maxItems = maxItems;
            this.allowed = allowed;
        }

        @Override
        public int maxItems() {
            return maxItems;
        }

        @Override
        public boolean canInsert(Inventory inv, Resource res) {
            if (maxItems == 0) return false;
            if (allowed != null && !allowed.contains(res.type)) return false;
            return inv.itemCount() < maxItems;
        }

        @Override
        public boolean canExtract(Inventory inv, ResourceType type) {
            return inv.has(type);
        }
    }
}
