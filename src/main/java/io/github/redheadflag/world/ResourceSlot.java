package io.github.redheadflag.world;

public class ResourceSlot {
    private Resource resource;   // null = empty

    public ResourceSlot() {}
    public ResourceSlot(Resource res) {
        put(res);
    }

    public boolean isEmpty() {
        return resource == null;
    }

    public Resource get() {
        return resource;
    }

    public boolean put(Resource res) {
        if (!isEmpty()) return false;
        this.resource = res;
        return true;
    }

    public Resource remove() {
        Resource r = resource;
        resource = null;
        return r;
    }
}
