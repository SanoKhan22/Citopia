package com.citopia.model;

public enum VehicleType {
    DONKEY_CARAVAN("Donkey Caravan", 900, 12, 4, "Cheap desert starter"),
    CAMEL_TRAIN("Camel Train", 1_800, 24, 6, "Efficient desert freight"),
    FELUCCA_BOAT("Felucca Boat", 2_500, 18, 8, "Fast Nile passenger boat"),
    CARGO_BARGE("Cargo Barge", 4_200, 45, 4, "Heavy Nile cargo hauler");

    private final String displayName;
    private final int price;
    private final int capacity;
    private final int speed;
    private final String role;

    VehicleType(String displayName, int price, int capacity, int speed, String role) {
        this.displayName = displayName;
        this.price = price;
        this.capacity = capacity;
        this.speed = speed;
        this.role = role;
    }

    public String displayName() {
        return displayName;
    }

    public int price() {
        return price;
    }

    public int capacity() {
        return capacity;
    }

    public int speed() {
        return speed;
    }

    public String role() {
        return role;
    }
}
