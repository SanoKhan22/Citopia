package com.citopia.economy;

import com.citopia.transport.VehicleType;

/**
 * Utility class for basic economic calculations.
 */
public final class EconomyCalculator {

    private static final double BASE_REVENUE_PER_TILE = 4.0;

    private EconomyCalculator() {
    }

    public static double calculateDeliveryRevenue(double distance, int cargoUnits) {
        if (distance < 0) {
            throw new IllegalArgumentException("Distance cannot be negative");
        }
        if (cargoUnits < 0) {
            throw new IllegalArgumentException("Cargo units cannot be negative");
        }
        return distance * cargoUnits * BASE_REVENUE_PER_TILE;
    }

    public static double calculateOperatingCost(VehicleType vehicleType, int ticks) {
        if (ticks < 0) {
            throw new IllegalArgumentException("Ticks cannot be negative");
        }
        return vehicleType.getOperatingCostPerTick() * ticks;
    }
}
