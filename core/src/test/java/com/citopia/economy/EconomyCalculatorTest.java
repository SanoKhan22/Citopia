package com.citopia.economy;

import com.citopia.transport.VehicleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EconomyCalculatorTest {

    @Test
    @DisplayName("Delivery revenue scales with distance and cargo units")
    void testCalculateDeliveryRevenue() {
        double revenue = EconomyCalculator.calculateDeliveryRevenue(12, 10);
        assertEquals(480, revenue, 0.001);
    }

    @Test
    @DisplayName("Operating cost scales with ticks and vehicle type")
    void testCalculateOperatingCost() {
        double cost = EconomyCalculator.calculateOperatingCost(VehicleType.CAMEL_TRAIN, 8);
        assertEquals(28.0, cost, 0.001);
    }
}
