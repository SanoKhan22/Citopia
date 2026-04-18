package com.citopia.world.transport;

import com.badlogic.gdx.math.GridPoint2;
import com.citopia.world.CitySite;
import com.citopia.world.TileMap;
import com.citopia.world.pathfinding.AStarPathfinder;
import com.citopia.world.pathfinding.Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the generation and discovery of logical routes between cities.
 */
public class RouteNetwork {
    private final TileMap map;
    private final AStarPathfinder pathfinder;
    
    // Cached shortest routes between all pairs of established cities
    private final Map<CitySite, Map<CitySite, Route>> cachedShortestRoutes = new HashMap<>();

    public RouteNetwork(TileMap map) {
        this.map = map;
        this.pathfinder = new AStarPathfinder(map);
    }

    /**
     * Re-scans the entire map to find all possible routes between registered cities.
     * Caches the shortest route for every pair.
     */
    public void generateNetwork() {
        cachedShortestRoutes.clear();
        
        List<CitySite> cities = map.cities();
        for (CitySite origin : cities) {
            cachedShortestRoutes.put(origin, new HashMap<>());
            
            for (CitySite destination : cities) {
                if (origin == destination) continue;
                
                Route route = computeShortestRoute(origin, destination);
                if (route != null) {
                    cachedShortestRoutes.get(origin).put(destination, route);
                }
            }
        }
    }

    public Route getCachedRoute(CitySite origin, CitySite destination) {
        Map<CitySite, Route> destMap = cachedShortestRoutes.get(origin);
        if (destMap != null) {
            return destMap.get(destination);
        }
        return null;
    }

    /**
     * Dynamically computes the precise path between any two cities on the current road network.
     */
    public Route computeShortestRoute(CitySite origin, CitySite destination) {
        List<GridPoint2> validStarts = getCityRoadAccessPoints(origin);
        List<GridPoint2> validEnds = getCityRoadAccessPoints(destination);
        
        Path bestPath = null;

        for (GridPoint2 start : validStarts) {
            for (GridPoint2 end : validEnds) {
                Path candidate = pathfinder.findPath(start, end);
                if (candidate != null) {
                    if (bestPath == null || candidate.getLength() < bestPath.getLength()) {
                        bestPath = candidate;
                    }
                }
            }
        }

        if (bestPath == null) return null;

        return new Route(origin, destination, bestPath, generateRouteName(origin, destination, bestPath));
    }

    private List<GridPoint2> getCityRoadAccessPoints(CitySite city) {
        List<GridPoint2> accessPoints = new ArrayList<>();
        
        if (city.type == CitySite.CityType.CAPITAL) {
            // Capital roads terminate exactly at its core boundaries
            int half = CitySite.CORE_HALF_SIZE;
            accessPoints.add(new GridPoint2(city.centerX, city.centerY + half + 1)); // North exit
            accessPoints.add(new GridPoint2(city.centerX, city.centerY - half - 1)); // South exit
            accessPoints.add(new GridPoint2(city.centerX + half + 1, city.centerY)); // East exit
            accessPoints.add(new GridPoint2(city.centerX - half - 1, city.centerY)); // West exit
        } else {
            // Outer cities have roads converging precisely at their center coordinate
            accessPoints.add(new GridPoint2(city.centerX, city.centerY));
        }
        
        return accessPoints;
    }

    private String generateRouteName(CitySite origin, CitySite destination, Path path) {
        boolean involvesCapital = origin.type == CitySite.CityType.CAPITAL || destination.type == CitySite.CityType.CAPITAL;
        
        if (involvesCapital) {
            // Because capital is center, any path connecting to it directly is generally short spur (~60 tiles).
            if (path.getLength() < 100) {
                return origin.name + " to " + destination.name + " (Direct Spur)";
            } else {
                return origin.name + " to " + destination.name + " (Redirect Route)";
            }
        } else {
            // Between two outer cities
            if (path.getLength() > 250) {
                return origin.name + " to " + destination.name + " (Long Outer Ring)";
            } else {
                return origin.name + " to " + destination.name + " (Ring Connector)";
            }
        }
    }
}
