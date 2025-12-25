package io.github.redheadflag.world;

public abstract class Resource {
    public ResourceType type;
    protected int value;

    public Resource(ResourceType type, int value) {
        this.type = type;
        this.value = value;
    }

    public int getValue() { return value; }
    public int changeValue(int quantity) {
        if (quantity > value)
            return 0;
        value += quantity;
        return value;
    }
}
