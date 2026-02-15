package io.github.redheadflag.engine;

import io.github.redheadflag.world.GameGrid;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Game {
    private final GameGrid grid;
    private final Runnable requestRender;
    private final Runnable onStart;
    private long tickCount = 0;
    private ScheduledExecutorService scheduler;

    public Game(GameGrid grid,
        Runnable requestRender,
        Runnable onStart
    ) {
        this.grid = grid;
        this.requestRender = requestRender;
        this.onStart = onStart;
    }

    public void tick() {
        tickCount++;
        grid.tick(tickCount);
        requestRender.run();
    }

    /**
     * Start a fixed-rate tick loop (ticksPerSecond). Call stop() to stop.
     */
    public void start(int ticksPerSecond) {
        if (scheduler != null && !scheduler.isShutdown()) return;
        
        onStart.run();

        scheduler = Executors.newSingleThreadScheduledExecutor();
        long periodMs = 1000L / Math.max(1, ticksPerSecond);
        scheduler.scheduleAtFixedRate(this::tick, 0, periodMs, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
        }
    }
}
