package com.citopia.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Acts as the centralized state holder for the simulation data.
 * Does not contain any libGDX or rendering logic, pure data model.
 */
public class GameState {
    
    private final List<City> cities;
    private final List<Route> routes;
    private final List<Vehicle> vehicles;
    private final List<Cargo> availableCargo;
    
    private int currentMoney;
    private long currentTick;

    public GameState(int initialMoney) {
        if (initialMoney < 0) {
            throw new IllegalArgumentException("Initial money cannot be negative.");
        }
        this.cities = new ArrayList<>();
        this.routes = new ArrayList<>();
        this.vehicles = new ArrayList<>();
        this.availableCargo = new ArrayList<>();
        
        this.currentMoney = initialMoney;
        this.currentTick = 0;
    }

    public List<City> getCities() {
        return Collections.unmodifiableList(cities);
    }

    public void addCity(City city) {
        if (city != null) this.cities.add(city);
    }
    
    public List<Route> getRoutes() {
        return Collections.unmodifiableList(routes);
    }

    public void addRoute(Route route) {
        if (route != null) this.routes.add(route);
    }

    public List<Vehicle> getVehicles() {
        return Collections.unmodifiableList(vehicles);
    }

    public void addVehicle(Vehicle vehicle) {
        if (vehicle != null) this.vehicles.add(vehicle);
    }

    public List<Cargo> getAvailableCargo() {
        return Collections.unmodifiableList(availableCargo);
    }

    public void addCargo(Cargo cargo) {
        if (cargo != null) this.availableCargo.add(cargo);
    }

    public int getCurrentMoney() { return currentMoney; }
    
    public void addMoney(int amount) {
        this.currentMoney += amount;
    }
    
    public void subtractMoney(int amount) {
        this.currentMoney -= amount;
    }

    public long getCurrentTick() { return currentTick; }
    
    /**
     * Increments the game simulation tick.
     */
    public void advanceTick() {
        this.currentTick++;
    }

    @Override
    public String toString() {
        return String.format("GameState{money=%d, tick=%d, entities=[cities=%d, routes=%d, vehicles=%d, cargo=%d]}",
                currentMoney, currentTick, cities.size(), routes.size(), vehicles.size(), availableCargo.size());
    }
}
