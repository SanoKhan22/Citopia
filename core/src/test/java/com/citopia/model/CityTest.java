package com.citopia.model;

import com.citopia.map.TileMap;
import com.citopia.map.TileType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link City}.
 */
class CityTest {

    @Test
    @DisplayName("City is created with correct properties")
    void testCityCreation() {
        City city = new City("Budapest", 100f, 200f, 1750000);

        assertEquals("Budapest", city.getName());
        assertEquals(100f, city.getX());
        assertEquals(200f, city.getY());
        assertEquals(1750000, city.getPopulation());
    }

    @Test
    @DisplayName("City rejects null name")
    void testNullNameThrows() {
        assertThrows(IllegalArgumentException.class,
            () -> new City(null, 0, 0, 100));
    }

    @Test
    @DisplayName("City rejects blank name")
    void testBlankNameThrows() {
        assertThrows(IllegalArgumentException.class,
            () -> new City("  ", 0, 0, 100));
    }

    @Test
    @DisplayName("City rejects negative population")
    void testNegativePopulationThrows() {
        assertThrows(IllegalArgumentException.class,
            () -> new City("Test", 0, 0, -1));
    }

    @Test
    @DisplayName("Population can be updated")
    void testSetPopulation() {
        City city = new City("Debrecen", 300f, 150f, 200000);
        city.setPopulation(210000);
        assertEquals(210000, city.getPopulation());
    }

    @Test
    @DisplayName("Setting negative population throws")
    void testSetNegativePopulationThrows() {
        City city = new City("Szeged", 250f, 100f, 160000);
        assertThrows(IllegalArgumentException.class,
            () -> city.setPopulation(-5));
    }

    @Test
    @DisplayName("Distance between two cities is calculated correctly")
    void testDistanceTo() {
        City a = new City("A", 0f, 0f, 100);
        City b = new City("B", 3f, 4f, 200);
        assertEquals(5f, a.distanceTo(b), 0.001f);
    }

    @Test
    @DisplayName("Distance to self is zero")
    void testDistanceToSelf() {
        City city = new City("Solo", 10f, 20f, 500);
        assertEquals(0f, city.distanceTo(city), 0.001f);
    }

    @Test
    @DisplayName("toString contains city name")
    void testToString() {
        City city = new City("Pecs", 50f, 50f, 145000);
        assertTrue(city.toString().contains("Pecs"));
    }

    @Test
    @DisplayName("Tile coordinates are derived from world coordinates")
    void testTileCoordinates() {
        City city = new City("Tile City", 160f, 96f, 1000);

        assertEquals(2, city.getTileX());
        assertEquals(1, city.getTileY());
    }

    @Test
    @DisplayName("City placement validator accepts cities on desert settlement tiles")
    void testPlacementOnSand() {
        TileMap map = new TileMap(5, 5, TileType.SAND);
        City city = CityPlacementValidator.createTileAlignedCity("Test", 2, 3, 5000, map);

        assertEquals(2, city.getTileX());
        assertEquals(3, city.getTileY());
    }

    @Test
    @DisplayName("City placement validator rejects cities outside the map")
    void testPlacementOutOfBounds() {
        TileMap map = new TileMap(5, 5, TileType.SAND);

        assertThrows(IllegalArgumentException.class,
            () -> CityPlacementValidator.createTileAlignedCity("Test", 6, 1, 5000, map));
    }

    @Test
    @DisplayName("City placement validator rejects cities on non-settlement tiles")
    void testPlacementOnWaterThrows() {
        TileMap map = new TileMap(5, 5, TileType.SAND);
        map.setTile(1, 1, TileType.WATER);

        assertThrows(IllegalArgumentException.class,
            () -> CityPlacementValidator.createTileAlignedCity("Harbor", 1, 1, 5000, map));
    }
}
