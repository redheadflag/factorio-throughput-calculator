package io.github.redheadflag.world;

import java.util.Optional;

public class TransferService {
    public boolean transferOne(Inventory from, Inventory to, long tick) {
        Optional<Resource> opt = from.peekFirst();
        if (opt.isEmpty()) return false;

        Resource res = opt.get();

        // Your rule: an item moved this tick cannot move again
        if (res.movedThisTick(tick)) return false;

        if (!to.canAdd(res)) return false;

        // Now commit
        from.removeFirst();
        to.add(res);
        res.markMoved(tick);
        return true;
    }
}
