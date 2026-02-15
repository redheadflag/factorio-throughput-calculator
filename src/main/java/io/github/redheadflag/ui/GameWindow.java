package io.github.redheadflag.ui;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import javax.swing.*;

public class GameWindow {
    public static void show(GamePanel panel) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Factory Blueprint");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.add(panel);
            frame.pack();

            // ==== DEBUG ====
            // Get screen size
            // Get all screens
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice[] screens = ge.getScreenDevices();

            if (screens.length > 1) {
                GraphicsDevice secondScreen = screens[1];
                Rectangle bounds = secondScreen.getDefaultConfiguration().getBounds();

                Dimension windowSize = frame.getSize();
                int margin = 10;

                int x = bounds.x + bounds.width - windowSize.width - margin;
                int y = bounds.y + bounds.height - windowSize.height - margin;

                frame.setLocation(x, y);
            } else {
                // Fallback if only one screen
                frame.setLocationRelativeTo(null);
            }
            // ==== END DEBUG ====

            frame.setVisible(true);
            frame.toFront();
            frame.requestFocus();
        });
    }
}
