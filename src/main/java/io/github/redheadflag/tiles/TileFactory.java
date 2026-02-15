package io.github.redheadflag.tiles;

import io.github.redheadflag.world.Direction;
import io.github.redheadflag.world.ResourceType;

public class TileFactory {
    public static Tile parseTile(String s) {
        TileType type = TileType.fromCode(s.charAt(0));

        return switch (type) {
            case EMPTY -> new EmptyTile();
            case SOURCE -> {
                ResourceType resourceType = ResourceType.fromCode(s.charAt(1));
                int quantity = Integer.parseInt(s.substring(2));
                yield new SourceTile(resourceType, quantity);
            }
            case CONVEYOR_BELT -> {
                Direction direction = Direction.fromCode(s.charAt(1));
                yield new ConveyorBeltTile(direction);
            }
            case SPLITTER -> {
                Direction direction = Direction.fromCode(s.charAt(1));
                yield new SplitterTile(direction);
            }
            case ASSEMBLING_STATION_1 -> new AssemblingStation1Tile();
            case ASSEMBLING_STATION_2 -> new AssemblingStation2Tile();
            case CHEST -> new ChestTile();
            default -> throw new IllegalArgumentException("Unknown tile type '" + type + "' in tile string: \"" + s + "\"");
        };
    }
}
