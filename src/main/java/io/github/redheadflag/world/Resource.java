package io.github.redheadflag.world;

public class Resource {
    public ResourceType type;

    private long lastMovedTick = Long.MIN_VALUE;

    public Resource(ResourceType type) {
        this.type = type;
    }

    public boolean movedThisTick(long tick) {
        return lastMovedTick == tick;
    }

    public void markMoved(long tick) {
        this.lastMovedTick = tick;
    }
}
