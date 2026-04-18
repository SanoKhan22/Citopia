package com.citopia.world.transport;

import com.badlogic.gdx.math.GridPoint2;
import com.citopia.world.CitySite;
import com.citopia.world.MapGenerator;
import com.citopia.world.TileMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RouteNetworkTest {

    private TileMap map;
    private RouteNetwork network;

    private CitySite capital;
    private CitySite north;
    private CitySite south;
    private CitySite east;
    private CitySite west;

    @BeforeEach
    public void setup() {
        map = new TileMap(320, 320, 12345L); // standard game size
        MapGenerator.generate(map, 12345L);
        network = new RouteNetwork(map);
        network.generateNetwork();

        for (CitySite c : map.cities()) {
            switch (c.type) {
                case CAPITAL -> capital = c;
                case NORTH -> north = c;
                case SOUTH -> south = c;
                case EAST -> east = c;
                case WEST -> west = c;
            }
        }
    }

    @Test
    public void testCapitalToNorthDirect() {
        Route r = network.getCachedRoute(capital, north);
        assertNotNull(r, "Should find route from Capital to North");
        assertEquals(capital, r.getOrigin());
        assertEquals(north, r.getDestination());
        // Distance is approx 60 (80 - 20)
        assertTrue(r.getLength() >= 58 && r.getLength() <= 62, "Direct spur should be ~60 tiles " + r.getLength());
        assertTrue(r.getName().contains("Direct Spur"));
    }

    @Test
    public void testNorthToEastRing() {
        Route r = network.getCachedRoute(north, east);
        assertNotNull(r);
        // Distance along L-shape is 80 out + 80 down = approx 160 tiles
        assertTrue(r.getLength() >= 158 && r.getLength() <= 162, "Quarter ring should be ~160 tiles: " + r.getLength());
        assertTrue(r.getName().contains("Ring Connector"));
    }

    @Test
    public void testNorthToSouthLongRing() {
        Route r = network.getCachedRoute(north, south);
        assertNotNull(r);
        // North to South must traverse the ring via East or West.
        // N -> E is ~160. E -> S is ~160. Total ~320 tiles.
        assertTrue(r.getLength() >= 316 && r.getLength() <= 324, "Long ring from N to S should be ~320 tiles: " + r.getLength());
        assertTrue(r.getName().contains("Long Outer Ring"));
    }

    @Test
    public void testAllPairsHaveRoutes() {
        for (CitySite origin : map.cities()) {
            for (CitySite dest : map.cities()) {
                if (origin != dest) {
                    assertNotNull(network.getCachedRoute(origin, dest));
                }
            }
        }
    }
}
