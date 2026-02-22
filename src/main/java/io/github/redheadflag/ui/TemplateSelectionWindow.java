package io.github.redheadflag.ui;

import io.github.redheadflag.engine.Game;
import io.github.redheadflag.world.GameGrid;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class TemplateSelectionWindow {

    private static final String GRID_TEMPLATES_FOLDER = "grid_templates/";

    public static void show() {
        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Select Grid Template");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new GridLayout(0, 1, 5, 5));

            File folder = new File(GRID_TEMPLATES_FOLDER);
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt"));

            if (files != null) {
                java.util.Arrays.sort(files, (a, b) ->
                        a.getName().compareToIgnoreCase(b.getName()));
            }

            if (files != null) {
                for (File file : files) {
                    JButton button = new JButton(file.getName());

                    button.addActionListener(e -> {
                        launchGame(file.getName());
                        frame.dispose();
                    });

                    buttonPanel.add(button);
                }
            }

            frame.add(new JScrollPane(buttonPanel), BorderLayout.CENTER);

            frame.setSize(400, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static void launchGame(String filename) {
        GameGrid grid = GameGrid.fromFile(GRID_TEMPLATES_FOLDER + filename);
        GamePanel panel = new GamePanel(grid);

        Game game = new Game(
                grid,
                panel::repaint,
                () -> GameWindow.show(panel)
        );

        game.start(1);
    }
}
