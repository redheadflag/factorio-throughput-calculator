package io.github.redheadflag.world;

public enum Direction {
    UP('U', "Turned Up"),
    DOWN('D', "Turned Down"),
    LEFT('L', "Turned Left"),
    RIGHT('R', "Turned Right");

    private final char code;        // short code for parsing
    private final String description; // human-readable description

    Direction(char code, String description) {
        this.code = code;
        this.description = description;
    }

    public char getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static Direction fromCode(char code) {
        for (Direction d : Direction.values()) {
            if (d.code == code) {
                return d;
            }
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }

    @Override
    public String toString() {
        return description;
    }
}
