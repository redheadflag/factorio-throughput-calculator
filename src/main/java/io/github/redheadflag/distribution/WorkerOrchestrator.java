package io.github.redheadflag.distribution;

import java.util.List;

import io.github.redheadflag.world.TickContext;

public class WorkerOrchestrator {
    public static boolean tick(List<Worker> workers, TickContext tickContext) {
        boolean isUpdated = false;
        for (Worker w : workers) {
            if (w.tick(tickContext)) {
                isUpdated = true;
            }
        }
        if (isUpdated) {
            tickContext.logUpdate();
        }
        return isUpdated;
    }
}
