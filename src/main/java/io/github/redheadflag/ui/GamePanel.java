package io.github.redheadflag.ui;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.stream.Collectors;

import io.github.redheadflag.tiles.Tile;
import io.github.redheadflag.world.GameGrid;
import io.github.redheadflag.world.ResourceType;

public class GamePanel extends JPanel {
    private final GameGrid grid;
    private final int tileSize = 100; // pixels per tile

    public GamePanel(GameGrid grid) {
        this.grid = grid;
        setPreferredSize(new Dimension(
                grid.getWidth() * tileSize,
                grid.getHeight() * tileSize
        ));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {

                Tile tile = grid.getTileAt(x, y);

                int px = x * tileSize;
                int py = y * tileSize;

                // Draw grid background
                g.setColor(Color.DARK_GRAY);
                g.drawRect(px, py, tileSize, tileSize);

                if (tile == null) continue;

                var img = TileSprites.get(tile.getType());
                if (img != null) {
                    g.drawImage(img, px, py, tileSize, tileSize, null);
                }

                if (!tile.getInventory().isEmpty()) {

                    // Count resources by type
                    Map<ResourceType, Long> counts =
                            tile.getInventory().getSlots().stream()
                                    .filter(s -> !s.isEmpty())
                                    .collect(Collectors.groupingBy(
                                            s -> s.get().type,
                                            Collectors.counting()
                                    ));

                    // Build a short debug string — first entry only for now
                    String text = counts.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .map(e -> e.getKey().name() + ": " + e.getValue())
                        .collect(Collectors.joining("\n"));

                    // Draw text bottom-right
                    g2.setFont(new Font("Monospaced", Font.BOLD, 16));
                    FontMetrics fm = g2.getFontMetrics();

                    String[] lines = text.split("\n");
                    int lineHeight = fm.getHeight();

                    int ty = py + tileSize - 2;

                    for (int i = 0; i < lines.length; i++) {
                        String line = lines[lines.length - 1 - i]; // bottom-up
                        int textWidth = fm.stringWidth(line);
                        int tx = px + tileSize - textWidth - 2;

                        int yLine = ty - (i * lineHeight);

                        // shadow
                        g2.setColor(Color.BLACK);
                        g2.drawString(line, tx + 1, yLine + 1);

                        g2.setColor(Color.YELLOW);
                        g2.drawString(line, tx, yLine);
                    }
                }
            }
        }
    }
}
