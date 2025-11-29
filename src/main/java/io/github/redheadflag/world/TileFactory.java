package io.github.redheadflag.world;

public class TileFactory {
    public static Tile parseTile(String s) {
        TileType type = TileType.fromCode(s.charAt(0));

        return switch (type) {
            case EMPTY -> new EmptyTile();
            case SOURCE -> {
                ResourceType resourceType = ResourceType.fromCode(s.charAt(1));
                int quantity = Integer.parseInt(s.substring(2));
                if (resourceType == ResourceType.IRON)
                    yield new IronSourceTile(quantity);
                else if (resourceType == ResourceType.COPPER)
                    yield new CopperSourceTile(quantity);
                else
                    throw new IllegalArgumentException("Unknown resource code '" + s.charAt(1) + "' in tile string: \"" + s + "\"");
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
