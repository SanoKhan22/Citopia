package com.citopia.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VehiclePurchaseTest {

    @Test
    void purchaseVehicleDeductsGoldAndStoresVehicle() {
        PlayerState state = new PlayerState();

        Vehicle vehicle = state.purchaseVehicle(VehicleType.CAMEL_TRAIN, "Capital");

        assertNotNull(vehicle);
        assertEquals(VehicleType.CAMEL_TRAIN, vehicle.type());
        assertEquals("Capital", vehicle.homeCityName());
        assertEquals("Camel Train #1", vehicle.displayName());
        assertEquals(25_000 - VehicleType.CAMEL_TRAIN.price(), state.getGold());
        assertEquals(1, state.getVehicleCount());
        assertSame(vehicle, state.getVehicles().get(0));
    }

    @Test
    void purchaseVehicleRejectsUnaffordableVehicleWithoutChangingInventory() {
        PlayerState state = new PlayerState();
        assertTrue(state.spend(34_000), "Setup should leave player near max debt");
        int goldBeforePurchase = state.getGold();

        Vehicle vehicle = state.purchaseVehicle(VehicleType.CARGO_BARGE, "South City");

        assertNull(vehicle);
        assertEquals(goldBeforePurchase, state.getGold());
        assertEquals(0, state.getVehicleCount());
        assertTrue(state.getVehicles().isEmpty());
    }

    @Test
    void purchaseVehicleValidatesRequiredInputs() {
        PlayerState state = new PlayerState();

        assertThrows(IllegalArgumentException.class, () -> state.purchaseVehicle(null, "Capital"));
        assertThrows(IllegalArgumentException.class, () -> state.purchaseVehicle(VehicleType.DONKEY_CARAVAN, null));
        assertThrows(IllegalArgumentException.class, () -> state.purchaseVehicle(VehicleType.DONKEY_CARAVAN, "   "));
    }

    @Test
    void ownedVehiclesCannotBeMutatedFromOutsidePlayerState() {
        PlayerState state = new PlayerState();
        state.purchaseVehicle(VehicleType.DONKEY_CARAVAN, "West City");

        assertThrows(UnsupportedOperationException.class,
                () -> state.getVehicles().add(new Vehicle(99, VehicleType.FELUCCA_BOAT, "North City")));
        assertEquals(1, state.getVehicleCount());
    }
}
