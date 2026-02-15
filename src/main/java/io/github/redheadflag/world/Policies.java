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
        private final int maxSlots;
        private final Set<ResourceType> allowed;

        private BasicPolicy(int maxSlots, Set<ResourceType> allowed) {
            this.maxSlots = maxSlots;
            this.allowed = allowed;
        }

        @Override
        public int maxSlots() {
            return maxSlots;
        }

        @Override
        public boolean canInsert(Inventory inv, Resource res) {
            if (maxSlots == 0) return false;
            if (allowed != null && !allowed.contains(res.type)) return false;
            return inv.itemCount() < maxSlots; // inventory tracks how many items stored
        }

        @Override
        public boolean canExtract(Inventory inv, ResourceType type) {
            // You can add more rules later (locks, filters, etc.)
            return inv.has(type);
        }
    }
}
