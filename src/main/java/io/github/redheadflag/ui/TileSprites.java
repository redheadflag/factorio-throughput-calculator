package io.github.redheadflag.ui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import javax.imageio.ImageIO;

import io.github.redheadflag.tiles.SourceTile;
import io.github.redheadflag.tiles.Tile;
import io.github.redheadflag.tiles.TileType;
import io.github.redheadflag.world.ResourceType;

public class TileSprites {

    private static final Map<TileType, BufferedImage> sprites =
            new EnumMap<>(TileType.class);

    private static final Map<ResourceType, BufferedImage> sourceSprites =
            new EnumMap<>(ResourceType.class);

    static {
        try {
            sprites.put(TileType.CONVEYOR_BELT, load("conveyor.png"));
            sprites.put(TileType.SPLITTER, load("splitter.png"));
            sprites.put(TileType.ASSEMBLING_STATION_1, load("assembler1.png"));
            sprites.put(TileType.ASSEMBLING_STATION_2, load("assembler2.png"));
            sprites.put(TileType.CHEST, load("chest.png"));
            sprites.put(TileType.EMPTY, load("empty.png"));

            sprites.put(TileType.SOURCE, load("source.png"));

            sourceSprites.put(ResourceType.IRON, load("source-iron.png"));
            sourceSprites.put(ResourceType.COPPER, load("source-copper.png"));

        } catch (IOException e) {
            throw new RuntimeException("Failed loading sprites", e);
        }
    }

    private static BufferedImage load(String name) throws IOException {
        var stream = TileSprites.class.getResourceAsStream("/sprites/" + name);
        if (stream == null) {
            throw new IOException("Sprite not found: " + name);
        }
        return ImageIO.read(stream);
    }

    public static BufferedImage get(Tile tile) {
        if (tile == null) {
            return null;
        }

        if (tile instanceof SourceTile source) {
            BufferedImage variant = sourceSprites.get(source.getResourceType());
            if (variant != null) {
                return variant;
            }
        }

        return sprites.get(tile.getType());
    }
}
