package com.citopia.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RouteTest {

    @Test
    @DisplayName("Route computes distance from source to target")
    void computesDistance() {
        City source = new City("A", 0f, 0f, 1000);
        City target = new City("B", 3f, 4f, 1500);

        Route route = new Route(source, target, 20);

        assertSame(source, route.getSource());
        assertSame(target, route.getTarget());
        assertEquals(5f, route.getDistance(), 0.001f);
        assertEquals(20, route.getCostPerTick());
    }

    @Test
    @DisplayName("Route rejects null source")
    void rejectsNullSource() {
        City target = new City("B", 3f, 4f, 1500);

        assertThrows(IllegalArgumentException.class,
                () -> new Route(null, target, 5));
    }

    @Test
    @DisplayName("Route rejects null target")
    void rejectsNullTarget() {
        City source = new City("A", 0f, 0f, 1000);

        assertThrows(IllegalArgumentException.class,
                () -> new Route(source, null, 5));
    }

    @Test
    @DisplayName("Route rejects source and target as same object")
    void rejectsSameEndpoints() {
        City city = new City("A", 0f, 0f, 1000);

        assertThrows(IllegalArgumentException.class,
                () -> new Route(city, city, 5));
    }
}
