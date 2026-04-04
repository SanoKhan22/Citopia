package com.citopia.transport;

import com.citopia.model.Route;

import java.util.Objects;

/**
 * Represents one active transport trip for a vehicle on a route.
 */
public class TransportTrip {

    private final Vehicle vehicle;
    private final Route route;
    private final int cargoUnits;
    private int progressSteps;
    private boolean completed;

    public TransportTrip(Vehicle vehicle, Route route, int cargoUnits) {
        this.vehicle = Objects.requireNonNull(vehicle, "Vehicle cannot be null");
        this.route = Objects.requireNonNull(route, "Route cannot be null");
        if (cargoUnits <= 0) {
            throw new IllegalArgumentException("Cargo units must be positive");
        }
        if (cargoUnits > vehicle.getType().getCargoCapacity()) {
            throw new IllegalArgumentException("Cargo exceeds vehicle capacity");
        }
        this.cargoUnits = cargoUnits;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public Route getRoute() {
        return route;
    }

    public int getCargoUnits() {
        return cargoUnits;
    }

    public int getProgressSteps() {
        return progressSteps;
    }

    public boolean isCompleted() {
        return completed;
    }

    public int requiredSteps() {
        return Math.max(0, route.getTilePath().size() - 1);
    }

    public void advanceOneStep() {
        if (completed) {
            return;
        }

        progressSteps++;
        if (progressSteps >= requiredSteps()) {
            completed = true;
            vehicle.setCurrentCity(route.getDestination());
        }
    }
}
