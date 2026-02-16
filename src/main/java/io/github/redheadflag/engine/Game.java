package io.github.redheadflag.engine;

import io.github.redheadflag.ui.StatisticsWindow;
import io.github.redheadflag.world.GameGrid;
import io.github.redheadflag.world.TickContext;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

public class Game {
    private final GameGrid grid;
    private final Runnable requestRender;
    private final Runnable onStart;
    private final TickContext tickContext;
    private ScheduledExecutorService scheduler;

    public Game(
        GameGrid grid,
        Runnable requestRender,
        Runnable onStart
    ) {
        this.grid = grid;
        this.requestRender = requestRender;
        this.onStart = onStart;
        this.tickContext = new TickContext();
    }

    public void tick() {
        tickContext.incrTickCount();
        grid.tick(tickContext);
        requestRender.run();
        if (tickContext.checkEndCondition()) {
            stop();
            SwingUtilities.invokeLater(() -> 
                StatisticsWindow.show(tickContext, grid)
            );
        }
    }

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
