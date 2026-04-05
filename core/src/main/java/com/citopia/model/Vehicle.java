package com.citopia.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Vehicle {
    public enum VehicleState {
        IDLE, IN_TRANSIT, LOADING, UNLOADING
    }

    private final String id;
    private final String name;
    private final float speed;
    private final int capacity;
    
    private final List<Cargo> loadedCargo;
    private City currentLocation;
    private VehicleState state;

    public Vehicle(String id, String name, float speed, int capacity, City initialLocation) {
        if (id == null || name == null || initialLocation == null) {
            throw new IllegalArgumentException("Id, name, and initial location cannot be null.");
        }
        if (capacity <= 0 || speed <= 0) {
            throw new IllegalArgumentException("Capacity and speed must be strictly positive.");
        }
        this.id = id;
        this.name = name;
        this.speed = speed;
        this.capacity = capacity;
        this.currentLocation = initialLocation;
        this.state = VehicleState.IDLE;
        this.loadedCargo = new ArrayList<>();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public float getSpeed() { return speed; }
    public int getCapacity() { return capacity; }
    
    public City getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(City location) { this.currentLocation = location; }
    
    public VehicleState getState() { return state; }
    public void setState(VehicleState state) { this.state = state; }
    
    public List<Cargo> getLoadedCargo() {
        return Collections.unmodifiableList(loadedCargo);
    }
    
    public int getCurrentLoadWeight() {
        return loadedCargo.stream().mapToInt(Cargo::getWeight).sum();
    }

    public boolean loadCargo(Cargo cargo) {
        if (getCurrentLoadWeight() + cargo.getWeight() <= capacity) {
            return loadedCargo.add(cargo);
        }
        return false; // Not enough capacity
    }
    
    public boolean unloadCargo(Cargo cargo) {
        return loadedCargo.remove(cargo);
    }

    @Override
    public String toString() {
        return String.format("Vehicle{id='%s', name='%s', state=%s, loc=%s, load=%d/%d}",
                id, name, state, currentLocation != null ? currentLocation.getName() : "null", 
                getCurrentLoadWeight(), capacity);
    }
}
