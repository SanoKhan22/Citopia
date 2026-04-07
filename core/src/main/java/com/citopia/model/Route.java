package com.citopia.model;

import com.citopia.map.GridPoint;

import java.util.List;
import java.util.Objects;

/**
 * Represents a planned route between two cities.
 */
public class Route {

    private final City origin;
    private final City destination;
    private final List<GridPoint> tilePath;
    private final double distance;
    private final double estimatedCost;

    public Route(City origin, City destination, List<GridPoint> tilePath, double estimatedCost) {
        this.origin = Objects.requireNonNull(origin, "Origin city cannot be null");
        this.destination = Objects.requireNonNull(destination, "Destination city cannot be null");
        this.tilePath = List.copyOf(Objects.requireNonNull(tilePath, "Tile path cannot be null"));

        if (this.tilePath.isEmpty()) {
            throw new IllegalArgumentException("Tile path cannot be empty");
        }
        if (estimatedCost < 0) {
            throw new IllegalArgumentException("Estimated cost cannot be negative");
        }

        this.distance = Math.max(0, this.tilePath.size() - 1);
        this.estimatedCost = estimatedCost;
    }

    public City getOrigin() {
        return origin;
    }

    public City getDestination() {
        return destination;
    }

    public List<GridPoint> getTilePath() {
        return tilePath;
    }

    public double getDistance() {
        return distance;
    }

    public double getEstimatedCost() {
        return estimatedCost;
    }
}
