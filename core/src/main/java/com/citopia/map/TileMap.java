package com.citopia.map;

import java.util.Objects;

/**
 * Stores a fixed-size grid of terrain tiles.
 */
public class TileMap {

    private final int width;
    private final int height;
    private final Tile[][] tiles;

    public TileMap(int width, int height, TileType defaultType) {
        if (width <= 0) {
            throw new IllegalArgumentException("Map width must be positive");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("Map height must be positive");
        }

        Objects.requireNonNull(defaultType, "Default tile type cannot be null");

        this.width = width;
        this.height = height;
        this.tiles = new Tile[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tiles[y][x] = new Tile(defaultType);
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Tile getTile(int x, int y) {
        validateCoordinates(x, y);
        return tiles[y][x];
    }

    public void setTile(int x, int y, TileType type) {
        validateCoordinates(x, y);
        tiles[y][x] = new Tile(Objects.requireNonNull(type, "Tile type cannot be null"));
    }

    private void validateCoordinates(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IllegalArgumentException(
                String.format("Tile coordinates out of bounds: (%d, %d)", x, y)
            );
        }
    }
}
