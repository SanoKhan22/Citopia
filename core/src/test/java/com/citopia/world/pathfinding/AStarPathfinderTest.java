package com.citopia.world.pathfinding;

import com.badlogic.gdx.math.GridPoint2;
import com.citopia.world.TileMap;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AStarPathfinderTest {

    @Test
    void findPathReturnsStraightRoadWithAllSteps() {
        TileMap map = new TileMap(96, 96, 7L);
        placeRoadLine(map, 5, 5, 8, 5);

        Path path = new AStarPathfinder(map).findPath(new GridPoint2(5, 5), new GridPoint2(8, 5));

        assertNotNull(path);
        assertEquals(3, path.getLength());
        assertEquals(List.of(
                new GridPoint2(5, 5),
                new GridPoint2(6, 5),
                new GridPoint2(7, 5),
                new GridPoint2(8, 5)), path.getSteps());
    }

    @Test
    void findPathReturnsNullWhenRoadGapBreaksNetwork() {
        TileMap map = new TileMap(96, 96, 11L);
        map.placeRoad(5, 8);
        map.placeRoad(6, 8);
        map.placeRoad(8, 8);
        map.placeRoad(9, 8);

        Path path = new AStarPathfinder(map).findPath(new GridPoint2(5, 8), new GridPoint2(9, 8));

        assertNull(path);
    }

    @Test
    void findPathUsesAvailableDetourInsteadOfCrossingGap() {
        TileMap map = new TileMap(96, 96, 13L);
        map.placeRoad(5, 12);
        map.placeRoad(6, 12);
        map.placeRoad(6, 13);
        map.placeRoad(7, 13);
        map.placeRoad(8, 13);
        map.placeRoad(8, 12);
        map.placeRoad(9, 12);

        Path path = new AStarPathfinder(map).findPath(new GridPoint2(5, 12), new GridPoint2(9, 12));

        assertNotNull(path);
        assertEquals(6, path.getLength());
        assertFalse(path.getSteps().contains(new GridPoint2(7, 12)));
        assertTrue(path.getSteps().contains(new GridPoint2(7, 13)));
    }

    @Test
    void returnedPathStepsCannotBeMutatedByCallers() {
        TileMap map = new TileMap(96, 96, 17L);
        placeRoadLine(map, 5, 15, 7, 15);
        Path path = new AStarPathfinder(map).findPath(new GridPoint2(5, 15), new GridPoint2(7, 15));

        assertNotNull(path);
        assertThrows(UnsupportedOperationException.class,
                () -> path.getSteps().add(new GridPoint2(99, 99)));
        assertEquals(2, path.getLength());
    }

    private void placeRoadLine(TileMap map, int startX, int startY, int endX, int endY) {
        int dx = Integer.compare(endX, startX);
        int dy = Integer.compare(endY, startY);
        int x = startX;
        int y = startY;
        while (x != endX || y != endY) {
            map.placeRoad(x, y);
            x += dx;
            y += dy;
        }
        map.placeRoad(endX, endY);
    }
}
