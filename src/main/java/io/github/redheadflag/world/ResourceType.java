package io.github.redheadflag.world;

public enum ResourceType {
    IRON('I', "Iron"),
    COPPER('C', "Copper"),
    COPPER_WIRE('W', "Copper Wire"),
    INDUCTOR('I', "Inductor");

    private final char code;
    private final String description;

    ResourceType(char code, String description) {
        this.code = code;
        this.description = description;
    }

    public char getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }

    // Parse from code to ResourceType
    public static ResourceType fromCode(char code) {
        for (ResourceType r : ResourceType.values()) {
            if (r.code == code) {
                return r;
            }
        }
        throw new IllegalArgumentException("Unknown resource code: " + code);
    }
}
