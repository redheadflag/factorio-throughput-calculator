package io.github.redheadflag.ui;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import io.github.redheadflag.tiles.Tile;
import io.github.redheadflag.tiles.TileType;
import io.github.redheadflag.world.GameGrid;
import io.github.redheadflag.world.Resource;
import io.github.redheadflag.world.ResourceType;
import io.github.redheadflag.world.TickContext;
import io.github.redheadflag.world.Updatable;

public class StatisticsWindow {

    public static void show(TickContext ctx, GameGrid grid) {
        JFrame frame = new JFrame("Game Statistics");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));

        textArea.setText(buildStats(ctx, grid));

        frame.add(new JScrollPane(textArea));
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static String buildStats(TickContext ctx, GameGrid grid) {
        StringBuilder sb = new StringBuilder();

        sb.append("Game finished!\n\n");
        sb.append("Total ticks: ").append(ctx.tickCount()).append("\n");
        sb.append("Produced items:\n");

        Map<ResourceType, Integer> totals = new HashMap<>();

        for (int y = grid.getHeight()-1; y >= 0; y--) {
            for (int x = grid.getWidth()-1; x >= 0; x--) {
                Tile t = grid.getTileAt(x, y);
                if (t.getType() == TileType.CHEST) {
                    for (Resource r : t.getInventory().getItems()) {
                        totals.merge(r.type, 1, Integer::sum);
                    }
                }
            }
        }

        if (totals.isEmpty()) {
            sb.append("  (no resources stored)\n");
        } else {
            for (Map.Entry<ResourceType, Integer> entry : totals.entrySet()) {
                sb.append(" - ")
                .append(entry.getKey())
                .append(": ")
                .append(entry.getValue())
                .append("\n");
            }
        }

        return sb.toString();
    }
}
