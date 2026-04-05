package com.citopia.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    @Test
    @DisplayName("GameState rejects negative initial money")
    void rejectsNegativeInitialMoney() {
        assertThrows(IllegalArgumentException.class, () -> new GameState(-1));
    }

    @Test
    @DisplayName("GameState starts with empty collections and tick zero")
    void startsWithDefaults() {
        GameState state = new GameState(100);

        assertEquals(100, state.getCurrentMoney());
        assertEquals(0L, state.getCurrentTick());
        assertTrue(state.getCities().isEmpty());
        assertTrue(state.getRoutes().isEmpty());
        assertTrue(state.getVehicles().isEmpty());
        assertTrue(state.getAvailableCargo().isEmpty());
    }

    @Test
    @DisplayName("GameState add methods append non-null values and ignore null")
    void addMethodsBehavior() {
        GameState state = new GameState(100);
        City a = new City("A", 0f, 0f, 1000);
        City b = new City("B", 3f, 4f, 1200);
        Route route = new Route(a, b, 10);
        Vehicle vehicle = new Vehicle("v1", "Truck", 2f, 20, a);
        Cargo cargo = new Cargo("c1", Cargo.CargoType.GOODS, 5, a, b);

        state.addCity(a);
        state.addCity(null);
        state.addRoute(route);
        state.addRoute(null);
        state.addVehicle(vehicle);
        state.addVehicle(null);
        state.addCargo(cargo);
        state.addCargo(null);

        assertEquals(1, state.getCities().size());
        assertEquals(1, state.getRoutes().size());
        assertEquals(1, state.getVehicles().size());
        assertEquals(1, state.getAvailableCargo().size());
    }

    @Test
    @DisplayName("GameState exposed lists are unmodifiable")
    void exposedListsUnmodifiable() {
        GameState state = new GameState(100);

        List<City> cities = state.getCities();
        assertThrows(UnsupportedOperationException.class,
                () -> cities.add(new City("X", 0f, 0f, 1)));
    }

    @Test
    @DisplayName("GameState money and tick mutation works")
    void moneyAndTickMutation() {
        GameState state = new GameState(100);

        state.addMoney(50);
        state.subtractMoney(30);
        state.advanceTick();
        state.advanceTick();

        assertEquals(120, state.getCurrentMoney());
        assertEquals(2L, state.getCurrentTick());
    }
}
