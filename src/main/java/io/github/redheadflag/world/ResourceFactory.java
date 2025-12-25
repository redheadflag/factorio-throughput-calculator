package io.github.redheadflag.world;

public class ResourceFactory {
    public static Resource createResource(ResourceType type, int value) {
        switch(type) {
            case IRON:
                return new Iron(value);
            case COPPER:
                return new Copper(value);
            default:
                throw new IllegalArgumentException("Unknown resource type: " + type);
        }
    }
}
