package com.citopia.model;

public class Cargo {
    public enum CargoType {
        PASSENGER, MAIL, GOODS
    }

    private final String id;
    private final CargoType type;
    private final int weight;
    private final City origin;
    private final City destination;

    public Cargo(String id, CargoType type, int weight, City origin, City destination) {
        if (origin == null || destination == null) {
            throw new IllegalArgumentException("Origin and destination cannot be null.");
        }
        this.id = id;
        this.type = type;
        this.weight = weight;
        this.origin = origin;
        this.destination = destination;
    }

    public String getId() { return id; }
    public CargoType getType() { return type; }
    public int getWeight() { return weight; }
    public City getOrigin() { return origin; }
    public City getDestination() { return destination; }

    @Override
    public String toString() {
        return String.format("Cargo{id='%s', type=%s, weight=%d, route=%s->%s}",
                id, type, weight, origin.getName(), destination.getName());
    }
}
