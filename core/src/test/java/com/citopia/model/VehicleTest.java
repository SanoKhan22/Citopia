package com.citopia.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VehicleTest {

    @Test
    @DisplayName("Vehicle constructor initializes defaults")
    void constructorDefaults() {
        City initial = new City("A", 0f, 0f, 1000);

        Vehicle vehicle = new Vehicle("v1", "Truck", 2.5f, 50, initial);

        assertEquals("v1", vehicle.getId());
        assertEquals("Truck", vehicle.getName());
        assertEquals(2.5f, vehicle.getSpeed());
        assertEquals(50, vehicle.getCapacity());
        assertSame(initial, vehicle.getCurrentLocation());
        assertEquals(Vehicle.VehicleState.IDLE, vehicle.getState());
        assertEquals(0, vehicle.getCurrentLoadWeight());
    }

    @Test
    @DisplayName("Vehicle rejects invalid constructor arguments")
    void rejectsInvalidConstructorArgs() {
        City initial = new City("A", 0f, 0f, 1000);

        assertThrows(IllegalArgumentException.class, () -> new Vehicle(null, "Truck", 2f, 10, initial));
        assertThrows(IllegalArgumentException.class, () -> new Vehicle("v1", null, 2f, 10, initial));
        assertThrows(IllegalArgumentException.class, () -> new Vehicle("v1", "Truck", 2f, 10, null));
        assertThrows(IllegalArgumentException.class, () -> new Vehicle("v1", "Truck", 0f, 10, initial));
        assertThrows(IllegalArgumentException.class, () -> new Vehicle("v1", "Truck", 1f, 0, initial));
    }

    @Test
    @DisplayName("Vehicle loads cargo within capacity and rejects overflow")
    void loadCargoRespectsCapacity() {
        City a = new City("A", 0f, 0f, 1000);
        City b = new City("B", 10f, 0f, 1000);
        Vehicle vehicle = new Vehicle("v1", "Truck", 2f, 10, a);

        Cargo c1 = new Cargo("c1", Cargo.CargoType.GOODS, 6, a, b);
        Cargo c2 = new Cargo("c2", Cargo.CargoType.GOODS, 4, a, b);
        Cargo c3 = new Cargo("c3", Cargo.CargoType.GOODS, 1, a, b);

        assertTrue(vehicle.loadCargo(c1));
        assertTrue(vehicle.loadCargo(c2));
        assertEquals(10, vehicle.getCurrentLoadWeight());
        assertFalse(vehicle.loadCargo(c3));
        assertEquals(10, vehicle.getCurrentLoadWeight());
    }

    @Test
    @DisplayName("Vehicle unloadCargo removes loaded item")
    void unloadCargoRemovesItem() {
        City a = new City("A", 0f, 0f, 1000);
        City b = new City("B", 10f, 0f, 1000);
        Vehicle vehicle = new Vehicle("v1", "Truck", 2f, 10, a);
        Cargo cargo = new Cargo("c1", Cargo.CargoType.GOODS, 6, a, b);

        vehicle.loadCargo(cargo);
        assertTrue(vehicle.unloadCargo(cargo));
        assertEquals(0, vehicle.getCurrentLoadWeight());
        assertFalse(vehicle.unloadCargo(cargo));
    }

    @Test
    @DisplayName("Vehicle loaded cargo list is unmodifiable")
    void loadedCargoUnmodifiable() {
        City a = new City("A", 0f, 0f, 1000);
        Vehicle vehicle = new Vehicle("v1", "Truck", 2f, 10, a);

        List<Cargo> loaded = vehicle.getLoadedCargo();
        assertThrows(UnsupportedOperationException.class, () -> loaded.add(null));
    }
}
