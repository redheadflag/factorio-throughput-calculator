package io.github.redheadflag.engine;

import io.github.redheadflag.world.GameGrid;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Game {
    private final GameGrid grid;
    private long tickCount = 0;
    private ScheduledExecutorService scheduler;

    public Game(GameGrid grid) {
        this.grid = grid;
    }

    public void tick() {
        tickCount++;
        grid.tick(tickCount);
        grid.display();
    }

    /**
     * Start a fixed-rate tick loop (ticksPerSecond). Call stopTicks() to stop.
     */
    public void startTicks(int ticksPerSecond) {
        if (scheduler != null && !scheduler.isShutdown()) return;
        scheduler = Executors.newSingleThreadScheduledExecutor();
        long periodMs = 1000L / Math.max(1, ticksPerSecond);
        scheduler.scheduleAtFixedRate(this::tick, 0, periodMs, TimeUnit.MILLISECONDS);
    }

    public void stopTicks() {
        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
        }
    }
}
