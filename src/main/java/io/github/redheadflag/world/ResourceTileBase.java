package io.github.redheadflag.world;

public abstract class ResourceTileBase extends Tile {
    protected ResourceType resourceType;
    protected int quantity;

    public ResourceTileBase(ResourceType resourceType, int quantity) {
        super(TileType.SOURCE);
        this.resourceType = resourceType;
        this.quantity = quantity;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return getType().getDescription() + " [" + resourceType.getDescription() + " x" + quantity + "]";
    }
}
