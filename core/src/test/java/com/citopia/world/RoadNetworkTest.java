package com.citopia.world;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the preset road network and permanent road mechanics.
 *
 * Covers:
 *  1. Capital + 4 outer cities are registered at expected positions
 *  2. Corridor tiles are marked as roads (hasRoad)
 *  3. Corridor tiles are marked as permanent (isPermanentRoad)
 *  4. Player-built roads are NOT permanent
 *  5. Permanent roads cannot be demolished (removeRoad is a no-op)
 *  6. Regular player roads CAN be demolished
 *  7. Corridor is exactly 3 tiles wide (no wider, no narrower)
 *  8. Corridors don't overwrite city core tiles — road stops at CORE_HALF_SIZE edge
 *  9. All 4 corridors meet within the capital bounding box
 * 10. No road tiles placed outside map bounds after generation
 */
public class RoadNetworkTest {

    // Use a map big enough to hold 2× the outer city distance on each side
    private static final int MAP_SIZE = 256;
    private static final long SEED = 42L;

    private TileMap map;
    private CitySite capital;

    @BeforeEach
    void setUp() {
        map = new TileMap(MAP_SIZE, MAP_SIZE, SEED);
        capital = map.capital();
        assertNotNull(capital, "Capital city must exist after generation");
    }

    // ── 1. City registration ──────────────────────────────────────────────────

    @Test
    void testFiveCitiesRegistered() {
        List<CitySite> cities = map.cities();
        assertEquals(5, cities.size(), "Must have exactly 5 cities: 1 capital + 4 outer");
    }

    @Test
    void testCapitalAtMapCenter() {
        int midX = MAP_SIZE / 2;
        int midY = MAP_SIZE / 2;
        assertEquals(midX, capital.centerX, "Capital X must be map center");
        assertEquals(midY, capital.centerY, "Capital Y must be map center");
    }

    @Test
    void testOuterCitiesOnCardinalAxes() {
        CitySite north = cityOf(CitySite.CityType.NORTH);
        CitySite south = cityOf(CitySite.CityType.SOUTH);
        CitySite east  = cityOf(CitySite.CityType.EAST);
        CitySite west  = cityOf(CitySite.CityType.WEST);

        // N/S share capital's X; E/W share capital's Y
        assertEquals(capital.centerX, north.centerX, "North city must share capital X");
        assertEquals(capital.centerX, south.centerX, "South city must share capital X");
        assertEquals(capital.centerY, east.centerY,  "East city must share capital Y");
        assertEquals(capital.centerY, west.centerY,  "West city must share capital Y");

        // North > capital in Y; South < capital in Y
        assertTrue(north.centerY > capital.centerY, "North city must be above capital");
        assertTrue(south.centerY < capital.centerY, "South city must be below capital");
        assertTrue(east.centerX  > capital.centerX, "East city must be right of capital");
        assertTrue(west.centerX  < capital.centerX, "West city must be left of capital");
    }

    // ── 2 & 3. Road and permanent flags on the N-S axis ──────────────────────

    @Test
    void testNorthCorridorHasRoadsAndIsPermanent() {
        CitySite north = cityOf(CitySite.CityType.NORTH);
        int axisX  = capital.centerX;
        int fromY  = capital.centerY + CitySite.CORE_HALF_SIZE + 1;
        int toY    = north.centerY   - CitySite.CORE_HALF_SIZE - 1;

        // Sample the centre column at every 10th tile
        for (int y = fromY; y <= toY; y += 10) {
            assertTrue(map.hasRoad(axisX, y),
                    "North corridor centre must be a road at y=" + y);
            assertTrue(map.isPermanentRoad(axisX, y),
                    "North corridor centre must be permanent at y=" + y);
        }
    }

    @Test
    void testSouthCorridorHasRoadsAndIsPermanent() {
        CitySite south = cityOf(CitySite.CityType.SOUTH);
        int axisX  = capital.centerX;
        int fromY  = south.centerY   + CitySite.CORE_HALF_SIZE + 1;
        int toY    = capital.centerY - CitySite.CORE_HALF_SIZE - 1;

        for (int y = fromY; y <= toY; y += 10) {
            assertTrue(map.hasRoad(axisX, y),
                    "South corridor centre must be a road at y=" + y);
            assertTrue(map.isPermanentRoad(axisX, y),
                    "South corridor centre must be permanent at y=" + y);
        }
    }

    @Test
    void testEastCorridorHasRoadsAndIsPermanent() {
        CitySite east = cityOf(CitySite.CityType.EAST);
        int axisY  = capital.centerY;
        int fromX  = capital.centerX + CitySite.CORE_HALF_SIZE + 1;
        int toX    = east.centerX    - CitySite.CORE_HALF_SIZE - 1;

        for (int x = fromX; x <= toX; x += 10) {
            assertTrue(map.hasRoad(x, axisY),
                    "East corridor centre must be a road at x=" + x);
            assertTrue(map.isPermanentRoad(x, axisY),
                    "East corridor centre must be permanent at x=" + x);
        }
    }

    @Test
    void testWestCorridorHasRoadsAndIsPermanent() {
        CitySite west = cityOf(CitySite.CityType.WEST);
        int axisY  = capital.centerY;
        int fromX  = west.centerX    + CitySite.CORE_HALF_SIZE + 1;
        int toX    = capital.centerX - CitySite.CORE_HALF_SIZE - 1;

        for (int x = fromX; x <= toX; x += 10) {
            assertTrue(map.hasRoad(x, axisY),
                    "West corridor centre must be a road at x=" + x);
            assertTrue(map.isPermanentRoad(x, axisY),
                    "West corridor centre must be permanent at x=" + x);
        }
    }

    // ── 4. Player roads are NOT permanent ────────────────────────────────────

    @Test
    void testPlayerRoadNotPermanent() {
        // Place a player road somewhere empty (far from any city or corridor)
        int px = 5, py = 5;
        assertFalse(map.hasRoad(px, py));
        map.placeRoad(px, py);
        assertTrue(map.hasRoad(px, py), "Player road must exist after placement");
        assertFalse(map.isPermanentRoad(px, py), "Player road must NOT be permanent");
    }

    // ── 5. Permanent roads can't be demolished ────────────────────────────────

    @Test
    void testPermanentRoadCannotBeDemolished() {
        // Find a permanent road tile on the east corridor centre
        int axisY = capital.centerY;
        int testX = capital.centerX + CitySite.CORE_HALF_SIZE + 5;

        assertTrue(map.isPermanentRoad(testX, axisY), "Precondition: tile must be permanent");

        map.removeRoad(testX, axisY); // should silently do nothing
        assertTrue(map.hasRoad(testX, axisY),
                "Permanent road must still exist after removeRoad() call");
    }

    // ── 6. Regular player roads CAN be demolished ─────────────────────────────

    @Test
    void testPlayerRoadCanBeDemolished() {
        int px = 5, py = 5;
        map.placeRoad(px, py);
        assertTrue(map.hasRoad(px, py));

        map.removeRoad(px, py);
        assertFalse(map.hasRoad(px, py), "Regular road must be gone after removeRoad()");
    }

    // ── 7. Corridor is exactly 3 tiles wide ──────────────────────────────────

    @Test
    void testNorthCorridorIsThreeTilesWide() {
        int axisX = capital.centerX;
        int testY = capital.centerY + CitySite.CORE_HALF_SIZE + 5; // well into corridor

        // The 3 tiles: axisX-1, axisX, axisX+1 must be roads
        assertTrue(map.hasRoad(axisX - 1, testY), "Left edge of 3-wide corridor must be road");
        assertTrue(map.hasRoad(axisX,     testY), "Centre of 3-wide corridor must be road");
        assertTrue(map.hasRoad(axisX + 1, testY), "Right edge of 3-wide corridor must be road");
        // Tile just outside the corridor must NOT be a preset road
        // (could be player-placed, but out-of-the-box it should be empty)
        assertFalse(map.isPermanentRoad(axisX - 2, testY), "2 tiles outside centre must NOT be permanent");
        assertFalse(map.isPermanentRoad(axisX + 2, testY), "2 tiles outside centre must NOT be permanent");
    }

    @Test
    void testEastCorridorIsThreeTilesWide() {
        int axisY = capital.centerY;
        int testX = capital.centerX + CitySite.CORE_HALF_SIZE + 5;

        assertTrue(map.hasRoad(testX, axisY - 1), "Bottom edge of 3-wide corridor must be road");
        assertTrue(map.hasRoad(testX, axisY),     "Centre must be road");
        assertTrue(map.hasRoad(testX, axisY + 1), "Top edge of 3-wide corridor must be road");
        assertFalse(map.isPermanentRoad(testX, axisY - 2), "2 tiles outside must NOT be permanent");
        assertFalse(map.isPermanentRoad(testX, axisY + 2), "2 tiles outside must NOT be permanent");
    }

    // ── 8. Corridors don't start inside the city core ─────────────────────────

    @Test
    void testCorridorDoesNotStartInsideCityCore() {
        int axisX = capital.centerX;
        int insideCore = capital.centerY + CitySite.CORE_HALF_SIZE - 1; // one tile inside edge

        // Tiles inside CORE_HALF_SIZE must NOT be marked permanent by the corridor
        // (they may be city zone, but road was not placed there by the corridor builder)
        assertFalse(map.isPermanentRoad(axisX, insideCore),
                "Corridor must not place permanent road inside city core");
    }

    // ── 9. All corridors are connected through the capital bounding box ────────

    @Test
    void testAllCorridorsMeetInsideCapital() {
        // The centre column and centre row must cross inside the capital core
        int axisX = capital.centerX;
        int axisY = capital.centerY;
        // The capital centre itself is inside CORE_HALF_SIZE — corridors stop at the edge,
        // so tiles *at* the capital centre won't be permanent road.
        // But tiles just outside the core on BOTH axes should be permanent.
        int justOutsideN = axisY + CitySite.CORE_HALF_SIZE + 1;
        int justOutsideS = axisY - CitySite.CORE_HALF_SIZE - 1;
        int justOutsideE = axisX + CitySite.CORE_HALF_SIZE + 1;
        int justOutsideW = axisX - CitySite.CORE_HALF_SIZE - 1;

        assertTrue(map.isPermanentRoad(axisX, justOutsideN), "N corridor starts just outside N capital edge");
        assertTrue(map.isPermanentRoad(axisX, justOutsideS), "S corridor starts just outside S capital edge");
        assertTrue(map.isPermanentRoad(justOutsideE, axisY), "E corridor starts just outside E capital edge");
        assertTrue(map.isPermanentRoad(justOutsideW, axisY), "W corridor starts just outside W capital edge");
    }

    // ── 10. No out-of-bounds roads ────────────────────────────────────────────

    @Test
    void testNoBoundsViolations() {
        // Scan all tiles at the edges — they must NOT have roads (corridors stay well inbound)
        for (int i = 0; i < MAP_SIZE; i++) {
            assertFalse(map.hasRoad(0, i),          "Left edge column must be road-free");
            assertFalse(map.hasRoad(MAP_SIZE - 1, i), "Right edge column must be road-free");
            assertFalse(map.hasRoad(i, 0),          "Bottom edge row must be road-free");
            assertFalse(map.hasRoad(i, MAP_SIZE - 1), "Top edge row must be road-free");
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private CitySite cityOf(CitySite.CityType type) {
        return map.cities().stream()
                .filter(c -> c.type == type)
                .findFirst()
                .orElseThrow(() -> new AssertionError("City of type " + type + " not found"));
    }
}
