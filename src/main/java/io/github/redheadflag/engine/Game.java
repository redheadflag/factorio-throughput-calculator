package io.github.redheadflag.engine;

import io.github.redheadflag.world.GameGrid;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Game {
    private final GameGrid grid;
    private final Runnable requestRender;
    private long tickCount = 0;
    private ScheduledExecutorService scheduler;

    public Game(GameGrid grid, Runnable requestRender) {
        this.grid = grid;
        this.requestRender = requestRender;
    }

    public void tick() {
        tickCount++;
        grid.tick(tickCount);
        requestRender.run();
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
