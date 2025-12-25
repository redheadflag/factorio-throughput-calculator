package io.github.redheadflag.world;

public enum Direction {
    UP('U', 0, -1),
    DOWN('D', 0, 1),
    LEFT('L', -1, 0),
    RIGHT('R', 1, 0);

    private final char code;        // short code for parsing
    private final int dx;
    private final int dy;

    Direction(char code, int dx, int dy) {
        this.code = code;
        this.dx = dx;
        this.dy = dy;
    }

    public char getCode() {
        return code;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
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
        return Character.toString(code);
    }
}
