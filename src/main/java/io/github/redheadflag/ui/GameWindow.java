package io.github.redheadflag.ui;

import javax.swing.*;

public class GameWindow {
    public static void show(GamePanel panel) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Factory Blueprint");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.add(panel);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
