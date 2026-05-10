package com.citopia.model;

import com.badlogic.gdx.math.GridPoint2;
import com.citopia.world.CitySite;
import com.citopia.world.pathfinding.Path;
import com.citopia.world.transport.Route;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class VehicleRouteAssignmentTest {

    private PlayerState state;
    private CitySite capital;
    private CitySite west;
    private CitySite east;

    @BeforeEach
    void setUp() {
        state = new PlayerState();
        capital = new CitySite(20, 20, CitySite.CityType.CAPITAL, "Capital");
        west = new CitySite(10, 20, CitySite.CityType.WEST, "West City");
        east = new CitySite(30, 20, CitySite.CityType.EAST, "East City");
    }

    @Test
    void assignVehicleToOwnedRouteStoresRouteOnVehicle() {
        Vehicle vehicle = state.purchaseVehicle(VehicleType.DONKEY_CARAVAN, "Capital");
        Route route = routeBetween(capital, west);
        state.addRoute(route);

        assertTrue(state.assignVehicleToRoute(vehicle.id(), route));

        assertSame(route, vehicle.assignedRoute());
        assertTrue(vehicle.hasRouteAssignment());
        assertEquals(1, state.getAssignedVehicleCount());
    }

    @Test
    void assignVehicleToRouteCanReassignVehicle() {
        Vehicle vehicle = state.purchaseVehicle(VehicleType.CAMEL_TRAIN, "Capital");
        Route firstRoute = routeBetween(capital, west);
        Route secondRoute = routeBetween(capital, east);
        state.addRoute(firstRoute);
        state.addRoute(secondRoute);

        assertTrue(state.assignVehicleToRoute(vehicle.id(), firstRoute));
        assertTrue(state.assignVehicleToRoute(vehicle.id(), secondRoute));

        assertSame(secondRoute, vehicle.assignedRoute());
        assertEquals(1, state.getAssignedVehicleCount());
    }

    @Test
    void assignVehicleToRouteRejectsUnknownVehicleWithoutChangingFleet() {
        Vehicle vehicle = state.purchaseVehicle(VehicleType.FELUCCA_BOAT, "East City");
        Route route = routeBetween(capital, east);
        state.addRoute(route);

        assertFalse(state.assignVehicleToRoute(99, route));

        assertFalse(vehicle.hasRouteAssignment());
        assertEquals(0, state.getAssignedVehicleCount());
    }

    @Test
    void assignVehicleToRouteValidatesOwnedRoute() {
        Vehicle vehicle = state.purchaseVehicle(VehicleType.DONKEY_CARAVAN, "West City");
        Route unownedRoute = routeBetween(capital, west);

        assertThrows(IllegalArgumentException.class,
                () -> state.assignVehicleToRoute(vehicle.id(), null));
        assertThrows(IllegalArgumentException.class,
                () -> state.assignVehicleToRoute(vehicle.id(), unownedRoute));
        assertFalse(vehicle.hasRouteAssignment());
    }

    private Route routeBetween(CitySite origin, CitySite destination) {
        return new Route(origin, destination, samplePath(), origin.name + " to " + destination.name);
    }

    private Path samplePath() {
        return new Path(List.of(new GridPoint2(0, 0), new GridPoint2(1, 0), new GridPoint2(2, 0)));
    }
}
