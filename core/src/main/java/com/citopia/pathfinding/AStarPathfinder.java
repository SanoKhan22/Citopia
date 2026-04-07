package com.citopia.pathfinding;

import com.citopia.map.GridPoint;
import com.citopia.map.TileMap;
import com.citopia.map.TileType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * A* pathfinding on a tile map using 4-directional movement.
 */
public class AStarPathfinder {

    public List<GridPoint> findPath(TileMap map, GridPoint start, GridPoint goal) {
        Objects.requireNonNull(map, "Map cannot be null");
        Objects.requireNonNull(start, "Start point cannot be null");
        Objects.requireNonNull(goal, "Goal point cannot be null");

        validatePoint(map, start);
        validatePoint(map, goal);

        if (!isWalkable(map, start) || !isWalkable(map, goal)) {
            return List.of();
        }

        if (start.equals(goal)) {
            return List.of(start);
        }

        Set<GridPoint> closedSet = new HashSet<>();
        Map<GridPoint, GridPoint> cameFrom = new HashMap<>();
        Map<GridPoint, Double> gScore = new HashMap<>();
        Map<GridPoint, Double> fScore = new HashMap<>();
        PriorityQueue<GridPoint> openSet = new PriorityQueue<>(Comparator.comparingDouble(point ->
            fScore.getOrDefault(point, Double.POSITIVE_INFINITY)
        ));

        gScore.put(start, 0.0);
        fScore.put(start, (double) heuristic(start, goal));
        openSet.add(start);

        while (!openSet.isEmpty()) {
            GridPoint current = openSet.poll();

            if (current.equals(goal)) {
                return reconstructPath(cameFrom, current);
            }

            closedSet.add(current);

            for (GridPoint neighbor : neighbors(map, current)) {
                if (closedSet.contains(neighbor)) {
                    continue;
                }

                double tentativeG = gScore.getOrDefault(current, Double.POSITIVE_INFINITY)
                    + movementCost(map, neighbor);

                if (tentativeG < gScore.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeG);
                    fScore.put(neighbor, tentativeG + heuristic(neighbor, goal));

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        return List.of();
    }

    private List<GridPoint> reconstructPath(Map<GridPoint, GridPoint> cameFrom, GridPoint current) {
        List<GridPoint> path = new ArrayList<>();
        path.add(current);

        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(current);
        }

        Collections.reverse(path);
        return path;
    }

    private List<GridPoint> neighbors(TileMap map, GridPoint point) {
        List<GridPoint> result = new ArrayList<>(4);
        addIfWalkable(map, result, point.x() + 1, point.y());
        addIfWalkable(map, result, point.x() - 1, point.y());
        addIfWalkable(map, result, point.x(), point.y() + 1);
        addIfWalkable(map, result, point.x(), point.y() - 1);
        return result;
    }

    private void addIfWalkable(TileMap map, List<GridPoint> result, int x, int y) {
        if (x < 0 || x >= map.getWidth() || y < 0 || y >= map.getHeight()) {
            return;
        }

        GridPoint point = new GridPoint(x, y);
        if (isWalkable(map, point)) {
            result.add(point);
        }
    }

    private boolean isWalkable(TileMap map, GridPoint point) {
        TileType tileType = map.getTile(point.x(), point.y()).getType();
        return tileType != TileType.WATER;
    }

    private double movementCost(TileMap map, GridPoint point) {
        TileType tileType = map.getTile(point.x(), point.y()).getType();
        return switch (tileType) {
            case OASIS -> 0.8;
            case SAND -> 1.0;
            case DUNE -> 1.5;
            case WATER -> Double.POSITIVE_INFINITY;
        };
    }

    private int heuristic(GridPoint point, GridPoint goal) {
        return Math.abs(point.x() - goal.x()) + Math.abs(point.y() - goal.y());
    }

    private void validatePoint(TileMap map, GridPoint point) {
        if (point.x() < 0 || point.x() >= map.getWidth() || point.y() < 0 || point.y() >= map.getHeight()) {
            throw new IllegalArgumentException("Point is outside map bounds: " + point);
        }
    }
}
