package com.citopia.transport;

/**
 * Supported vehicle categories and baseline economics.
 */
public enum VehicleType {
    DONKEY_CARAVAN(120, 2.0, 15),
    CAMEL_TRAIN(260, 3.5, 28),
    HORSE_CART(420, 5.0, 32),
    MODERN_TRUCK(900, 10.0, 60);

    private final int purchaseCost;
    private final double operatingCostPerTick;
    private final int cargoCapacity;

    VehicleType(int purchaseCost, double operatingCostPerTick, int cargoCapacity) {
        this.purchaseCost = purchaseCost;
        this.operatingCostPerTick = operatingCostPerTick;
        this.cargoCapacity = cargoCapacity;
    }

    public int getPurchaseCost() {
        return purchaseCost;
    }

    public double getOperatingCostPerTick() {
        return operatingCostPerTick;
    }

    public int getCargoCapacity() {
        return cargoCapacity;
    }
}
