package com.citopia.map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link TileMap}.
 */
class TileMapTest {

    @Test
    @DisplayName("Tile map is created with the expected size and default terrain")
    void testCreateTileMap() {
        TileMap map = new TileMap(4, 3, TileType.GRASS);

        assertEquals(4, map.getWidth());
        assertEquals(3, map.getHeight());
        assertEquals(TileType.GRASS, map.getTile(0, 0).getType());
        assertEquals(TileType.GRASS, map.getTile(3, 2).getType());
    }

    @Test
    @DisplayName("Setting a tile changes its terrain type")
    void testSetTile() {
        TileMap map = new TileMap(2, 2, TileType.GRASS);

        map.setTile(1, 1, TileType.WATER);

        assertEquals(TileType.WATER, map.getTile(1, 1).getType());
    }

    @Test
    @DisplayName("Creating a map with invalid dimensions throws")
    void testInvalidDimensionsThrow() {
        assertThrows(IllegalArgumentException.class, () -> new TileMap(0, 2, TileType.GRASS));
        assertThrows(IllegalArgumentException.class, () -> new TileMap(2, -1, TileType.GRASS));
    }

    @Test
    @DisplayName("Accessing an out-of-bounds tile throws")
    void testOutOfBoundsAccessThrows() {
        TileMap map = new TileMap(3, 3, TileType.GRASS);

        assertThrows(IllegalArgumentException.class, () -> map.getTile(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> map.getTile(0, 3));
        assertThrows(IllegalArgumentException.class, () -> map.setTile(3, 0, TileType.MOUNTAIN));
    }
}
