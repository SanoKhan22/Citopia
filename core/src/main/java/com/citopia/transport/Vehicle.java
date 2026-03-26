package com.citopia.transport;

import com.citopia.model.City;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a purchased vehicle in the transport company fleet.
 */
public class Vehicle {

    private final String id;
    private final VehicleType type;
    private City currentCity;

    public Vehicle(VehicleType type, City currentCity) {
        this.id = UUID.randomUUID().toString();
        this.type = Objects.requireNonNull(type, "Vehicle type cannot be null");
        this.currentCity = Objects.requireNonNull(currentCity, "Current city cannot be null");
    }

    public String getId() {
        return id;
    }

    public VehicleType getType() {
        return type;
    }

    public City getCurrentCity() {
        return currentCity;
    }

    public void setCurrentCity(City currentCity) {
        this.currentCity = Objects.requireNonNull(currentCity, "Current city cannot be null");
    }
}
