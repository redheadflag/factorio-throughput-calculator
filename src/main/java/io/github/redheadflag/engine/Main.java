package io.github.redheadflag.engine;

import io.github.redheadflag.ui.GamePanel;
import io.github.redheadflag.ui.GameWindow;
import io.github.redheadflag.world.GameGrid;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        GameGrid grid = GameGrid.fromFile("grid_templates/grid-1.txt");
        GamePanel panel = new GamePanel(grid);

        Game game = new Game(
            grid,
            panel::repaint,
            () -> GameWindow.show(panel)
        );
        
        game.start(10);
        Thread.sleep(60_000);
        game.stop();
    }
}