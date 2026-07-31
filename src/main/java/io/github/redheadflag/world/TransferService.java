package io.github.redheadflag.world;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

import io.github.redheadflag.tiles.Tile;

public class TransferService {
    public boolean transferOne(Tile fromTile, Tile toTile, TickContext tickContext) {
        return transferOneRecursive(fromTile, toTile, tickContext, new ArrayDeque<>());
    }

    private boolean transferOneRecursive(
        Tile fromTile,
        Tile toTile,
        TickContext tickContext,
        Deque<Tile> path
    ) {
        if (fromTile == null || toTile == null) return false;

        Inventory from = fromTile.getInventory();
        Inventory to = toTile.getInventory();
        if (from == null || to == null) return false;

        if (path.contains(fromTile)) return false;
        path.push(fromTile);

        try {
            Optional<Resource> opt = from.peekFirst();
            if (opt.isEmpty()) return false;

            Resource res = opt.get();

            if (res.movedThisTick(tickContext.tickCount())) return false;
            if (!from.getPolicy().canExtract(from, res.type)) return false;

            if (to.canAdd(res)) {
                return commitTransfer(from, to, res, tickContext);
            }

            if (!makeRoomInTarget(toTile, tickContext, path)) return false;
            if (!to.canAdd(res)) return false;

            return commitTransfer(from, to, res, tickContext);
        } finally {
            path.pop();
        }
    }

    private boolean makeRoomInTarget(Tile targetTile, TickContext tickContext, Deque<Tile> path) {
        for (Tile next : targetTile.getForwardTargets()) {
            if (transferOneRecursive(targetTile, next, tickContext, path)) {
                return true;
            }
        }
        return false;
    }

    private boolean commitTransfer(Inventory from, Inventory to, Resource res, TickContext tickContext) {
        Optional<Resource> removed = from.removeFirst();
        if (removed.isEmpty()) return false;

        boolean added = to.add(res);
        if (!added) {
            from.add(removed.get());
            return false;
        }

        res.markMoved(tickContext.tickCount());
        return true;
    }
}
