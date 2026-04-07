package com.citopia.model;

import com.citopia.map.GridPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RouteTest {

    @Test
    @DisplayName("Route computes distance from source to target")
    void computesDistance() {
        City source = new City("A", 0f, 0f, 1000);
        City target = new City("B", 3f, 4f, 1500);

        Route route = new Route(source, target, List.of(new GridPoint(0,0), new GridPoint(1,1), new GridPoint(2,2), new GridPoint(3,4)), 20.0);

        assertSame(source, route.getOrigin());
        assertSame(target, route.getDestination());
        assertEquals(3f, route.getDistance(), 0.001f);
        assertEquals(20.0, route.getEstimatedCost());
    }

    @Test
    @DisplayName("Route rejects null source")
    void rejectsNullSource() {
        City target = new City("B", 3f, 4f, 1500);

        assertThrows(NullPointerException.class,
                () -> new Route(null, target, List.of(new GridPoint(0,0), new GridPoint(1,1)), 5));
    }

    @Test
    @DisplayName("Route rejects null target")
    void rejectsNullTarget() {
        City source = new City("A", 0f, 0f, 1000);

        assertThrows(NullPointerException.class,
                () -> new Route(source, null, List.of(new GridPoint(0,0), new GridPoint(1,1)), 5));
    }

    @Test
    @DisplayName("Route rejects source and target as same object")
    void rejectsSameEndpoints() {
        City city = new City("A", 0f, 0f, 1000);
    }

    @Test
    @DisplayName("Route rejects empty tile path")
    void rejectsEmptyPath() {
        City source = new City("A", 0f, 0f, 1000);
        assertThrows(IllegalArgumentException.class,
                () -> new Route(source, source, List.of(), 5));
    }

    @Test
    @DisplayName("Route rejects negative cost")
    void rejectsNegativeCost() {
        City source = new City("A", 0f, 0f, 1000);
        assertThrows(IllegalArgumentException.class,
                () -> new Route(source, source, List.of(new GridPoint(0,0), new GridPoint(1,1)), -5));
    }
}
