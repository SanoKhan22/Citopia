package com.citopia.model;

import com.badlogic.gdx.math.GridPoint2;
import com.citopia.world.CitySite;
import com.citopia.world.pathfinding.Path;
import com.citopia.world.transport.Route;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerRouteTest {

    private PlayerState state;
    private CitySite capital;
    private CitySite north;

    @BeforeEach
    void setUp() {
        state = new PlayerState();
        capital = new CitySite(20, 20, CitySite.CityType.CAPITAL, "Capital");
        north = new CitySite(20, 80, CitySite.CityType.NORTH, "North City");
    }

    @Test
    void addRouteStoresNewCityConnection() {
        Route route = routeBetween(capital, north);

        assertTrue(state.addRoute(route));
        assertEquals(1, state.getRouteCount());
        assertSame(route, state.getRoutes().get(0));
    }

    @Test
    void addRouteRejectsDuplicateConnectionInEitherDirection() {
        assertTrue(state.addRoute(routeBetween(capital, north)));

        assertFalse(state.addRoute(routeBetween(capital, north)));
        assertFalse(state.addRoute(routeBetween(north, capital)));
        assertEquals(1, state.getRouteCount());
    }

    @Test
    void addRouteValidatesRequiredRouteAndEndpoints() {
        assertThrows(IllegalArgumentException.class, () -> state.addRoute(null));
        assertThrows(IllegalArgumentException.class,
                () -> state.addRoute(new Route(null, north, samplePath(), "Invalid")));
        assertThrows(IllegalArgumentException.class,
                () -> state.addRoute(new Route(capital, north, null, "Invalid")));
        assertThrows(IllegalArgumentException.class,
                () -> state.addRoute(new Route(capital, capital, samplePath(), "Loop")));
    }

    @Test
    void ownedRoutesCannotBeMutatedFromOutsidePlayerState() {
        state.addRoute(routeBetween(capital, north));

        assertThrows(UnsupportedOperationException.class,
                () -> state.getRoutes().add(routeBetween(north, capital)));
        assertEquals(1, state.getRouteCount());
    }

    private Route routeBetween(CitySite origin, CitySite destination) {
        return new Route(origin, destination, samplePath(), origin.name + " to " + destination.name);
    }

    private Path samplePath() {
        return new Path(List.of(new GridPoint2(0, 0), new GridPoint2(1, 0), new GridPoint2(2, 0)));
    }
}
