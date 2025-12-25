package io.github.redheadflag.engine;

import io.github.redheadflag.world.GameGrid;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        GameGrid grid = GameGrid.fromFile("grid.txt");
        Game game = new Game(grid);
        game.startTicks(10); // 10 ticks/sec
        Thread.sleep(60_000); // run for 60s (or keep running)
        game.stopTicks();
    }
}