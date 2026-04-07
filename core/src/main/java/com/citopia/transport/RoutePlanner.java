package com.citopia.transport;

import com.citopia.map.GridPoint;
import com.citopia.map.TileMap;
import com.citopia.model.City;
import com.citopia.model.Route;
import com.citopia.pathfinding.AStarPathfinder;

import java.util.List;
import java.util.Objects;

/**
 * Plans transport routes between cities.
 */
public class RoutePlanner {

    private static final double COST_PER_TILE = 15.0;

    private final AStarPathfinder pathfinder;

    public RoutePlanner(AStarPathfinder pathfinder) {
        this.pathfinder = Objects.requireNonNull(pathfinder, "Pathfinder cannot be null");
    }

    public Route planRoute(TileMap map, City origin, City destination) {
        Objects.requireNonNull(map, "Map cannot be null");
        Objects.requireNonNull(origin, "Origin city cannot be null");
        Objects.requireNonNull(destination, "Destination city cannot be null");

        GridPoint start = new GridPoint(origin.getTileX(), origin.getTileY());
        GridPoint goal = new GridPoint(destination.getTileX(), destination.getTileY());

        List<GridPoint> tilePath = pathfinder.findPath(map, start, goal);
        if (tilePath.isEmpty()) {
            throw new IllegalStateException("No valid route found between selected cities");
        }

        double estimatedCost = Math.max(0, tilePath.size() - 1) * COST_PER_TILE;
        return new Route(origin, destination, tilePath, estimatedCost);
    }
}
