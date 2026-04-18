package com.citopia.world.transport;

import com.citopia.world.CitySite;
import com.citopia.world.pathfinding.Path;

/**
 * Represents a computed route between two cities.
 */
public class Route {
    private final CitySite origin;
    private final CitySite destination;
    private final Path path;
    private final String name;

    public Route(CitySite origin, CitySite destination, Path path, String name) {
        this.origin = origin;
        this.destination = destination;
        this.path = path;
        this.name = name;
    }

    public CitySite getOrigin() {
        return origin;
    }

    public CitySite getDestination() {
        return destination;
    }

    public Path getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    /** Returns distance in tiles. */
    public int getLength() {
        return path.getLength();
    }
}
