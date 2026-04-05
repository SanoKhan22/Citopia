package com.citopia.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CargoTest {

    @Test
    @DisplayName("Cargo is created with expected properties")
    void cargoCreation() {
        City origin = new City("A", 0f, 0f, 1000);
        City destination = new City("B", 1f, 1f, 1500);

        Cargo cargo = new Cargo("cargo-1", Cargo.CargoType.GOODS, 40, origin, destination);

        assertEquals("cargo-1", cargo.getId());
        assertEquals(Cargo.CargoType.GOODS, cargo.getType());
        assertEquals(40, cargo.getWeight());
        assertSame(origin, cargo.getOrigin());
        assertSame(destination, cargo.getDestination());
    }

    @Test
    @DisplayName("Cargo rejects null origin")
    void rejectsNullOrigin() {
        City destination = new City("B", 1f, 1f, 1500);

        assertThrows(IllegalArgumentException.class,
                () -> new Cargo("cargo-2", Cargo.CargoType.MAIL, 10, null, destination));
    }

    @Test
    @DisplayName("Cargo rejects null destination")
    void rejectsNullDestination() {
        City origin = new City("A", 0f, 0f, 1000);

        assertThrows(IllegalArgumentException.class,
                () -> new Cargo("cargo-3", Cargo.CargoType.PASSENGER, 10, origin, null));
    }

    @Test
    @DisplayName("Cargo toString contains route endpoints")
    void toStringContainsEndpoints() {
        City origin = new City("A", 0f, 0f, 1000);
        City destination = new City("B", 1f, 1f, 1500);

        Cargo cargo = new Cargo("cargo-4", Cargo.CargoType.GOODS, 5, origin, destination);

        String output = cargo.toString();
        assertTrue(output.contains("A"));
        assertTrue(output.contains("B"));
    }
}
