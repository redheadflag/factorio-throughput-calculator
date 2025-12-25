package io.github.redheadflag.tiles;

public enum TileType {
    SOURCE('S', "Source"),
    CONVEYOR_BELT('B', "Conveyor Belt"),
    SPLITTER('T', "Splitter"),
    ASSEMBLING_STATION_1('1', "Assembling Station 1"),
    ASSEMBLING_STATION_2('2', "Assembling Station 2"),
    CHEST('C', "Chest"),
    EMPTY('.', "Empty");  // using a space or "" as code for empty

    private final char code;
    private final String description;

    TileType(char code, String description) {
        this.code = code;
        this.description = description;
    }

    public char getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    // Optional: override toString() for easy printing
    @Override
    public String toString() {
        return description;
    }

    // Parse from code to TileType
    public static TileType fromCode(char code) {
        for (TileType t : TileType.values()) {
            if (t.code == code) {
                return t;
            }
        }
        throw new IllegalArgumentException("Unknown tile code: " + code);
    }
}
