package com.citopia.world;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the ring-topology preset road network.
 *
 * Network topology:
 *  - 4 capital spurs (1 tile wide): one toward each outer city, never crossing inside capital
 *  - 4 L-shaped ring segments: N↔E (NE quadrant), E↔S (SE), S↔W (SW), W↔N (NW)
 *  - Result: all 5 cities connected, NO roads through the capital interior
 *
 * Test checklist:
 *  1.  Five cities registered at correct positions
 *  2.  No permanent road inside the capital core boundary
 *  3.  Capital spurs exist just outside the capital edge (N/S/E/W)
 *  4.  Capital spurs are 1 tile wide (not 3 wide)
 *  5.  NE L-segment horizontal leg exists (at N city Y, between N.e_edge and E.cx)
 *  6.  NE L-segment vertical leg exists (at E city X, between E.n_edge and N.cy)
 *  7.  SE, SW, NW L-segments exist (sampled)
 *  8.  L-segment corner tile exists and is permanent
 *  9.  All ring tiles are permanent (non-demolishable)
 * 10.  Player road is not permanent; permanent road cannot be demolished
 * 11.  No permanent road tiles along the old cross-through paths (capital centre row/col)
 * 12.  No out-of-bounds permanent roads
 */
public class RoadNetworkTest {

    private static final int MAP_SIZE = 256;
    private static final long SEED    = 42L;

    private TileMap           map;
    private CitySite          cap;
    private CitySite          N, S, E, W;
    private int               half; // CORE_HALF_SIZE

    @BeforeEach
    void setUp() {
        map  = new TileMap(MAP_SIZE, MAP_SIZE, SEED);
        cap  = findCity(CitySite.CityType.CAPITAL);
        N    = findCity(CitySite.CityType.NORTH);
        S    = findCity(CitySite.CityType.SOUTH);
        E    = findCity(CitySite.CityType.EAST);
        W    = findCity(CitySite.CityType.WEST);
        half = CitySite.CORE_HALF_SIZE;

        assertNotNull(cap, "Capital must exist");
        assertNotNull(N,   "North city must exist");
        assertNotNull(S,   "South city must exist");
        assertNotNull(E,   "East city must exist");
        assertNotNull(W,   "West city must exist");
    }

    // ── 1. Five cities at expected positions ─────────────────────────────────

    @Test
    void testFiveCitiesRegistered() {
        assertEquals(5, map.cities().size());
    }

    @Test
    void testCapitalAtCenter() {
        assertEquals(MAP_SIZE / 2, cap.centerX);
        assertEquals(MAP_SIZE / 2, cap.centerY);
    }

    @Test
    void testOuterCitiesOnCardinalAxes() {
        assertEquals(cap.centerX, N.centerX);
        assertEquals(cap.centerX, S.centerX);
        assertEquals(cap.centerY, E.centerY);
        assertEquals(cap.centerY, W.centerY);
        assertTrue(N.centerY > cap.centerY);
        assertTrue(S.centerY < cap.centerY);
        assertTrue(E.centerX > cap.centerX);
        assertTrue(W.centerX < cap.centerX);
    }

    // ── 2. NO permanent roads inside the capital core ────────────────────────

    @Test
    void testNoPermanentRoadInsideCapitalCore() {
        // Scan every tile strictly inside the capital core boundary
        for (int dy = -(half - 1); dy <= half - 1; dy++) {
            for (int dx = -(half - 1); dx <= half - 1; dx++) {
                int tx = cap.centerX + dx;
                int ty = cap.centerY + dy;
                assertFalse(map.isPermanentRoad(tx, ty),
                        "Capital core tile (" + tx + "," + ty + ") must NOT be a permanent road");
            }
        }
    }

    // ── 3. Capital spurs are present just outside the capital edges ───────────

    @Test
    void testCapitalNorthSpurExists() {
        // First tile north of the capital core edge
        int firstSpurTile = cap.centerY + half + 1;
        assertTrue(map.isPermanentRoad(cap.centerX, firstSpurTile),
                "North spur must start at capital N edge");
        // And extends toward N city — sample midway
        int mid = (cap.centerY + N.centerY) / 2;
        assertTrue(map.isPermanentRoad(cap.centerX, mid),
                "North spur must exist at midpoint toward N city");
    }

    @Test
    void testCapitalSouthSpurExists() {
        int firstSpurTile = cap.centerY - half - 1;
        assertTrue(map.isPermanentRoad(cap.centerX, firstSpurTile),
                "South spur must start at capital S edge");
        int mid = (cap.centerY + S.centerY) / 2;
        assertTrue(map.isPermanentRoad(cap.centerX, mid),
                "South spur must exist at midpoint toward S city");
    }

    @Test
    void testCapitalEastSpurExists() {
        int firstSpurTile = cap.centerX + half + 1;
        assertTrue(map.isPermanentRoad(firstSpurTile, cap.centerY),
                "East spur must start at capital E edge");
        int mid = (cap.centerX + E.centerX) / 2;
        assertTrue(map.isPermanentRoad(mid, cap.centerY),
                "East spur must exist at midpoint toward E city");
    }

    @Test
    void testCapitalWestSpurExists() {
        int firstSpurTile = cap.centerX - half - 1;
        assertTrue(map.isPermanentRoad(firstSpurTile, cap.centerY),
                "West spur must start at capital W edge");
        int mid = (cap.centerX + W.centerX) / 2;
        assertTrue(map.isPermanentRoad(mid, cap.centerY),
                "West spur must exist at midpoint toward W city");
    }

    // ── 4. Capital spurs are exactly 1 tile wide ─────────────────────────────

    @Test
    void testNorthSpurIsOneTileWide() {
        int testY = cap.centerY + half + 5; // well into the spur
        assertTrue(map.isPermanentRoad(cap.centerX,     testY), "Centre must be road");
        assertFalse(map.isPermanentRoad(cap.centerX + 1, testY), "1 tile right must NOT be permanent road");
        assertFalse(map.isPermanentRoad(cap.centerX - 1, testY), "1 tile left must NOT be permanent road");
    }

    @Test
    void testEastSpurIsOneTileWide() {
        int testX = cap.centerX + half + 5;
        assertTrue(map.isPermanentRoad(testX, cap.centerY),     "Centre must be road");
        assertFalse(map.isPermanentRoad(testX, cap.centerY + 1), "1 tile above must NOT be permanent road");
        assertFalse(map.isPermanentRoad(testX, cap.centerY - 1), "1 tile below must NOT be permanent road");
    }

    // ── 5 & 6. NE L-segment: horizontal + vertical legs ─────────────────────

    @Test
    void testNEHorizontalLegExists() {
        // Horizontal at N.centerY from N.e_edge to E.cx (going right past N city east edge)
        int sampleX = N.centerX + half + 5;           // just east of N city core
        assertTrue(map.isPermanentRoad(sampleX, N.centerY),
                "NE horizontal leg must have road just east of N city");
        // Also sample halfway toward East
        int midX = (N.centerX + E.centerX) / 2;
        assertTrue(map.isPermanentRoad(midX, N.centerY),
                "NE horizontal leg must have road at midpoint between N and E cities");
    }

    @Test
    void testNEVerticalLegExists() {
        // Vertical at E.centerX from N.cy down to E.n_edge
        int sampleY = E.centerY + half + 5;           // just north of E city core
        assertTrue(map.isPermanentRoad(E.centerX, sampleY),
                "NE vertical leg must have road just north of E city");
        // Also sample halfway
        int midY = (N.centerY + E.centerY) / 2;
        assertTrue(map.isPermanentRoad(E.centerX, midY),
                "NE vertical leg must have road at midpoint between N and E cities");
    }

    // ── 7. SE, SW, NW segments sampled ───────────────────────────────────────

    @Test
    void testSERingSegmentExists() {
        // SE: vertical at E.cx below E city, horizontal at S.cy east of S city
        int midY = (S.centerY + E.centerY) / 2;
        assertTrue(map.isPermanentRoad(E.centerX, midY),  "SE: vertical leg");
        int midX = (S.centerX + E.centerX) / 2;
        assertTrue(map.isPermanentRoad(midX, S.centerY),  "SE: horizontal leg");
    }

    @Test
    void testSWRingSegmentExists() {
        int midX = (S.centerX + W.centerX) / 2;
        assertTrue(map.isPermanentRoad(midX, S.centerY),  "SW: horizontal leg");
        int midY = (S.centerY + W.centerY) / 2;
        assertTrue(map.isPermanentRoad(W.centerX, midY),  "SW: vertical leg");
    }

    @Test
    void testNWRingSegmentExists() {
        int midY = (W.centerY + N.centerY) / 2;
        assertTrue(map.isPermanentRoad(W.centerX, midY),  "NW: vertical leg");
        int midX = (W.centerX + N.centerX) / 2;
        assertTrue(map.isPermanentRoad(midX, N.centerY),  "NW: horizontal leg");
    }

    // ── 8. L-bend corner tiles are permanent ─────────────────────────────────

    @Test
    void testNECornerIsPermanent() {
        // NE corner is at (E.cx, N.cy) — shared by both horizontal and vertical legs
        assertTrue(map.isPermanentRoad(E.centerX, N.centerY),
                "NE L-bend corner tile must be permanent");
    }

    @Test
    void testSECornerIsPermanent() {
        assertTrue(map.isPermanentRoad(E.centerX, S.centerY), "SE corner must be permanent");
    }

    @Test
    void testSWCornerIsPermanent() {
        assertTrue(map.isPermanentRoad(W.centerX, S.centerY), "SW corner must be permanent");
    }

    @Test
    void testNWCornerIsPermanent() {
        assertTrue(map.isPermanentRoad(W.centerX, N.centerY), "NW corner must be permanent");
    }

    // ── 9. Permanent → non-demolishable; player road → demolishable ──────────

    @Test
    void testPermanentRoadCannotBeDemolished() {
        // Pick a ring tile we've already verified is permanent
        int tx = E.centerX, ty = N.centerY; // NE corner
        assertTrue(map.isPermanentRoad(tx, ty));
        map.removeRoad(tx, ty);
        assertTrue(map.hasRoad(tx, ty), "Permanent road must survive removeRoad()");
    }

    @Test
    void testPlayerRoadCanBeDemolished() {
        int px = 5, py = 5;
        assertFalse(map.hasRoad(px, py));
        map.placeRoad(px, py);
        assertFalse(map.isPermanentRoad(px, py), "Player road must not be permanent");
        map.removeRoad(px, py);
        assertFalse(map.hasRoad(px, py), "Player road must be gone after removeRoad()");
    }

    // ── 11. Capital centre row and column have NO permanent roads ─────────────
    //   (the old design ran roads through (midX,midY); the new design must not)

    @Test
    void testCapitalCentreColumnHasNoRoadInsideCore() {
        // Column x=cap.cx, inside the core (y in [cap.cy-half+1 .. cap.cy+half-1])
        for (int dy = -(half - 1); dy <= half - 1; dy++) {
            assertFalse(map.isPermanentRoad(cap.centerX, cap.centerY + dy),
                    "Capital centre column must not carry a permanent road inside the core at dy=" + dy);
        }
    }

    @Test
    void testCapitalCentreRowHasNoRoadInsideCore() {
        for (int dx = -(half - 1); dx <= half - 1; dx++) {
            assertFalse(map.isPermanentRoad(cap.centerX + dx, cap.centerY),
                    "Capital centre row must not carry a permanent road inside the core at dx=" + dx);
        }
    }

    // ── 12. No permanent roads on map boundary rows/columns ───────────────────

    @Test
    void testNoPermanentRoadsOnMapEdge() {
        for (int i = 0; i < MAP_SIZE; i++) {
            assertFalse(map.isPermanentRoad(0,           i), "Left edge col must be clear");
            assertFalse(map.isPermanentRoad(MAP_SIZE - 1, i), "Right edge col must be clear");
            assertFalse(map.isPermanentRoad(i,           0), "Bottom edge row must be clear");
            assertFalse(map.isPermanentRoad(i, MAP_SIZE - 1), "Top edge row must be clear");
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private CitySite findCity(CitySite.CityType type) {
        return map.cities().stream()
                .filter(c -> c.type == type)
                .findFirst()
                .orElseThrow(() -> new AssertionError("City not found: " + type));
    }
}
