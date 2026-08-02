package io.github.redheadflag.ui;

import io.github.redheadflag.engine.Game;
import io.github.redheadflag.world.GameGrid;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public class TemplateSelectionWindow {

    private static final String GRID_TEMPLATES_FOLDER = "grid_templates/";
    private static final int DEFAULT_TICKS_PER_SECOND = 10;
    private static final long DEFAULT_SEED = 1337L;

    private static volatile int selectedTicksPerSecond = DEFAULT_TICKS_PER_SECOND;
    private static volatile long selectedSeed = DEFAULT_SEED;

    public static int getSelectedTicksPerSecond() {
        return selectedTicksPerSecond;
    }

    public static long getSelectedSeed() {
        return selectedSeed;
    }

    public static void show() {
        show(TemplateSelectionWindow::launchGame);
    }

    public static String showAndWait() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        String[] result = new String[1];

        show(filename -> {
            result[0] = GRID_TEMPLATES_FOLDER + filename;
            latch.countDown();
        });

        latch.await();
        return result[0];
    }

    public static void show(Consumer<String> onSelect) {
        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Select Grid Template");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            JPanel settingsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JSpinner ticksPerSecondSpinner = new JSpinner(
                    new SpinnerNumberModel(DEFAULT_TICKS_PER_SECOND, 1, 1000, 1));
            JSpinner seedSpinner = new JSpinner(
                    new SpinnerNumberModel(DEFAULT_SEED, Long.MIN_VALUE, Long.MAX_VALUE, 1L));
            settingsPanel.add(new JLabel("Ticks per second:"));
            settingsPanel.add(ticksPerSecondSpinner);
            settingsPanel.add(new JLabel("Seed:"));
            settingsPanel.add(seedSpinner);
            frame.add(settingsPanel, BorderLayout.NORTH);

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
                        selectedTicksPerSecond = (Integer) ticksPerSecondSpinner.getValue();
                        selectedSeed = ((Number) seedSpinner.getValue()).longValue();
                        onSelect.accept(file.getName());
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
                () -> GameWindow.show(panel),
                selectedSeed
        );

        game.start(selectedTicksPerSecond);
    }
}
