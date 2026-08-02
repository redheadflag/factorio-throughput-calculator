package io.github.redheadflag.distribution;

import io.github.redheadflag.tiles.Tile;

public record Edge(
    Tile source,
    Tile target,
    int sourcePartition,
    int targetPartition,
    BoundaryEdges.MovementType type
) {}
