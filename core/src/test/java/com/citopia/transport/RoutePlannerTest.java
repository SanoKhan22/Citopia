package com.citopia.transport;

import com.citopia.map.TileMap;
import com.citopia.map.TileType;
import com.citopia.model.City;
import com.citopia.model.CityPlacementValidator;
import com.citopia.model.Route;
import com.citopia.pathfinding.AStarPathfinder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoutePlannerTest {

    @Test
    @DisplayName("Route planner creates route with positive distance and cost")
    void testPlanRoute() {
        TileMap map = new TileMap(10, 10, TileType.SAND);
        City origin = CityPlacementValidator.createTileAlignedCity("A", 1, 1, 1000, map);
        City destination = CityPlacementValidator.createTileAlignedCity("B", 7, 6, 1200, map);

        RoutePlanner planner = new RoutePlanner(new AStarPathfinder());
        Route route = planner.planRoute(map, origin, destination);

        assertEquals(origin, route.getOrigin());
        assertEquals(destination, route.getDestination());
        assertTrue(route.getDistance() > 0);
        assertTrue(route.getEstimatedCost() > 0);
    }

    @Test
    @DisplayName("Route planner throws when destination is unreachable")
    void testPlanRouteUnreachable() {
        TileMap map = new TileMap(6, 6, TileType.SAND);
        for (int y = 0; y < map.getHeight(); y++) {
            map.setTile(3, y, TileType.WATER);
        }

        City origin = CityPlacementValidator.createTileAlignedCity("A", 1, 2, 1000, map);
        City destination = CityPlacementValidator.createTileAlignedCity("B", 5, 2, 1000, map);

        RoutePlanner planner = new RoutePlanner(new AStarPathfinder());

        assertThrows(IllegalStateException.class, () -> planner.planRoute(map, origin, destination));
    }
}
