package com.citopia.world.pathfinding;

import com.badlogic.gdx.math.GridPoint2;
import com.citopia.world.TileMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Standard A* tile-based pathfinding exclusively traversing road tiles.
 */
public class AStarPathfinder {

    private final TileMap map;
    
    // Orthogonal movement only (N, S, E, W)
    private static final int[][] DIRS = { {0, 1}, {0, -1}, {1, 0}, {-1, 0} };

    public AStarPathfinder(TileMap map) {
        this.map = map;
    }

    /**
     * Finds the shortest path along the road network.
     * @param start The starting tile coordinate
     * @param end The destination tile coordinate
     * @return Path containing coordinates from start to end (inclusive), or null if unreachable.
     */
    public Path findPath(GridPoint2 start, GridPoint2 end) {
        if (!map.hasRoad(end.x, end.y) && !start.equals(end)) {
            // Technically, if the destination is completely off-road, A* will never reach it.
            // But since cities might not have roads EXACTLY on their center, 
            // the system using this should pass the specific road entry point.
            // We'll proceed just in case, but usually this will fail if strictly adhering to hasRoad.
        }

        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.f));
        Map<GridPoint2, Node> allNodes = new HashMap<>();
        Set<GridPoint2> closedSet = new HashSet<>();

        Node startNode = new Node(start, null, 0, heuristic(start, end));
        openSet.add(startNode);
        allNodes.put(start, startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.pos.equals(end)) {
                return reconstructPath(current);
            }

            closedSet.add(current.pos);

            for (int[] dir : DIRS) {
                int nx = current.pos.x + dir[0];
                int ny = current.pos.y + dir[1];
                GridPoint2 neighborPos = new GridPoint2(nx, ny);

                if (nx < 0 || nx >= map.width() || ny < 0 || ny >= map.height()) continue;
                
                // Allow walking onto destination even if it's technically not flagged as a road?
                // For safety, the user of this API must provide the final road tile as `end`.
                if (!map.hasRoad(nx, ny) && !neighborPos.equals(end)) continue;

                if (closedSet.contains(neighborPos)) continue;

                int tentativeG = current.g + 1; // All road tiles cost 1
                Node neighborNode = allNodes.get(neighborPos);

                if (neighborNode == null || tentativeG < neighborNode.g) {
                    if (neighborNode == null) {
                        neighborNode = new Node(neighborPos, current, tentativeG, heuristic(neighborPos, end));
                        allNodes.put(neighborPos, neighborNode);
                        openSet.add(neighborNode);
                    } else {
                        // Priority Queue doesn't auto-update, so we remove and re-add
                        openSet.remove(neighborNode);
                        neighborNode.g = tentativeG;
                        neighborNode.parent = current;
                        neighborNode.f = neighborNode.g + neighborNode.h;
                        openSet.add(neighborNode);
                    }
                }
            }
        }

        return null; // No path found
    }

    private Path reconstructPath(Node endNode) {
        List<GridPoint2> steps = new ArrayList<>();
        Node current = endNode;
        while (current != null) {
            steps.add(current.pos);
            current = current.parent;
        }
        Collections.reverse(steps);
        return new Path(steps);
    }

    private int heuristic(GridPoint2 a, GridPoint2 b) {
        // Manhattan distance
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    private static class Node {
        final GridPoint2 pos;
        Node parent;
        int g; // cost from start
        int h; // heuristic to end
        int f; // g + h

        Node(GridPoint2 pos, Node parent, int g, int h) {
            this.pos = pos;
            this.parent = parent;
            this.g = g;
            this.h = h;
            this.f = g + h;
        }
    }
}
