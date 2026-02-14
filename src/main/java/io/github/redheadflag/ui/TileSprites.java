package io.github.redheadflag.ui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import javax.imageio.ImageIO;

import io.github.redheadflag.tiles.TileType;

public class TileSprites {
    private static final Map<TileType, BufferedImage> sprites = new EnumMap<>(TileType.class);

    static {
        try {
            sprites.put(TileType.CONVEYOR_BELT, load("conveyor.png"));
            sprites.put(TileType.SPLITTER, load("splitter.png"));
            sprites.put(TileType.ASSEMBLING_STATION_1, load("assembler1.png"));
            sprites.put(TileType.ASSEMBLING_STATION_2, load("assembler2.png"));
            sprites.put(TileType.SOURCE, load("source.png"));
            sprites.put(TileType.CHEST, load("chest.png"));
            sprites.put(TileType.EMPTY, load("empty.png"));
        } catch (IOException e) {
            throw new RuntimeException("Failed loading sprites", e);
        }
    }

    private static BufferedImage load(String name) throws IOException {
        return ImageIO.read(
            TileSprites.class.getResourceAsStream("/sprites/" + name)
        );
    }

    public static BufferedImage get(TileType type) {
        return sprites.get(type);
    }
}
