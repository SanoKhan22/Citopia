package com.citopia.pathfinding;

import com.citopia.map.GridPoint;
import com.citopia.map.TileMap;
import com.citopia.map.TileType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AStarPathfinderTest {

    @Test
    @DisplayName("Pathfinder returns a valid path between two reachable tiles")
    void testFindPathReachable() {
        TileMap map = new TileMap(6, 6, TileType.SAND);
        AStarPathfinder pathfinder = new AStarPathfinder();

        List<GridPoint> path = pathfinder.findPath(map, new GridPoint(0, 0), new GridPoint(5, 5));

        assertFalse(path.isEmpty());
        assertEquals(new GridPoint(0, 0), path.get(0));
        assertEquals(new GridPoint(5, 5), path.get(path.size() - 1));
    }

    @Test
    @DisplayName("Pathfinder returns empty when route is fully blocked by water")
    void testFindPathBlocked() {
        TileMap map = new TileMap(5, 5, TileType.SAND);
        for (int y = 0; y < map.getHeight(); y++) {
            map.setTile(2, y, TileType.WATER);
        }

        AStarPathfinder pathfinder = new AStarPathfinder();
        List<GridPoint> path = pathfinder.findPath(map, new GridPoint(0, 2), new GridPoint(4, 2));

        assertTrue(path.isEmpty());
    }

    @Test
    @DisplayName("Pathfinder validates out-of-bounds points")
    void testFindPathOutOfBoundsThrows() {
        TileMap map = new TileMap(3, 3, TileType.SAND);
        AStarPathfinder pathfinder = new AStarPathfinder();

        assertThrows(IllegalArgumentException.class,
            () -> pathfinder.findPath(map, new GridPoint(-1, 0), new GridPoint(2, 2)));
    }
}
