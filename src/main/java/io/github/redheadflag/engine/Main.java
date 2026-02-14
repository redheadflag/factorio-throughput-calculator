package io.github.redheadflag.engine;

import javax.swing.SwingUtilities;

import io.github.redheadflag.ui.GamePanel;
import io.github.redheadflag.ui.GameWindow;
import io.github.redheadflag.world.GameGrid;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        GameGrid grid = GameGrid.fromFile("grid.txt");
        GamePanel panel = new GamePanel(grid);
        Game game = new Game(grid, () -> SwingUtilities.invokeLater(panel::repaint));
        GameWindow.show(panel);
        game.startTicks(10);
        Thread.sleep(60_000);
        game.stopTicks();
    }
}