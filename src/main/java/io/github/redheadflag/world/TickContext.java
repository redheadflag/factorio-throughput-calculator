package io.github.redheadflag.world;

public class TickContext {
    private long tickCount;
    private long lastUpdateTick;

    public TickContext() {
        this.tickCount = 0;
        this.lastUpdateTick = 0;
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
