package com.citopia.map;

import java.util.Objects;

/**
 * Represents a single map tile.
 */
public class Tile {

    private final TileType type;

    public Tile(TileType type) {
        this.type = Objects.requireNonNull(type, "Tile type cannot be null");
    }

    public TileType getType() {
        return type;
    }
}
