package io.github.redheadflag.world;

import io.github.redheadflag.tiles.Tile;

public record TransferIntent(Tile from, Tile to) {}
