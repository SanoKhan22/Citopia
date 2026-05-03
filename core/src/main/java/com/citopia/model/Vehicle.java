package com.citopia.model;

import com.citopia.world.transport.Route;

public class Vehicle {

    private final int id;
    private final VehicleType type;
    private final String homeCityName;
    private Route assignedRoute;

    public Vehicle(int id, VehicleType type, String homeCityName) {
        if (id <= 0) {
            throw new IllegalArgumentException("Vehicle id must be positive");
        }
        if (type == null) {
            throw new IllegalArgumentException("Vehicle type is required");
        }
        if (homeCityName == null || homeCityName.isBlank()) {
            throw new IllegalArgumentException("Home city is required");
        }

        this.id = id;
        this.type = type;
        this.homeCityName = homeCityName.trim();
    }

    public int id() {
        return id;
    }

    public VehicleType type() {
        return type;
    }

    public String homeCityName() {
        return homeCityName;
    }

    public String displayName() {
        return type.displayName() + " #" + id;
    }

    public Route assignedRoute() {
        return assignedRoute;
    }

    public boolean hasRouteAssignment() {
        return assignedRoute != null;
    }

    void assignRoute(Route route) {
        if (route == null) {
            throw new IllegalArgumentException("Route is required");
        }
        assignedRoute = route;
    }
}
