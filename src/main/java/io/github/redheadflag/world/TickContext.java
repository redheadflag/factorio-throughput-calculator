package io.github.redheadflag.world;

import java.util.Random;

public class TickContext {
    private long tickCount;
    private long lastUpdateTick;
    private final Random random;

    public TickContext(long seed) {
        this.tickCount = 0;
        this.lastUpdateTick = 0;
        this.random = new Random(seed);
    }

    public Random random() {
        return random;
    }

    public long tickCount() {
        return tickCount;
    }

    public void incrTickCount() {
        this.tickCount++;
    }

    public void logUpdate() {
        this.lastUpdateTick = this.tickCount;
    }

    public boolean checkEndCondition() {
        if ((tickCount - lastUpdateTick) >= 5) {
            return true;
        }
        else {
            return false;
        }
    }
}
