package com.citopia.world;

import java.util.Random;

public class TileMap {

    private final int width;
    private final int height;
    private final byte[] groundTileIndices;

    public TileMap(int width, int height, long seed) {
        this.width = width;
        this.height = height;
        this.groundTileIndices = new byte[width * height];

        Random random = new Random(seed);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int value = random.nextInt(MapConfig.GROUND_TILE_VARIANTS);
                groundTileIndices[index(x, y)] = (byte) value;
            }
        }
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int groundTileIndex(int x, int y) {
        return groundTileIndices[index(x, y)] & 0xFF;
    }

    private int index(int x, int y) {
        return y * width + x;
    }
}
