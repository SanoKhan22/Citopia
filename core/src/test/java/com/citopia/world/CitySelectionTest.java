package com.citopia.world;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CitySelectionTest {

    private TileMap map;
    private CitySite capital;
    private CitySite north;

    @BeforeEach
    void setUp() {
        map = new TileMap(256, 256, 42L);
        capital = findCity(CitySite.CityType.CAPITAL);
        north = findCity(CitySite.CityType.NORTH);
    }

    @Test
    void returnsCityWhenClickingCityCenter() {
        assertSame(capital, map.cityAt(capital.centerX, capital.centerY));
        assertSame(north, map.cityAt(north.centerX, north.centerY));
    }

    @Test
    void returnsCityInsideFadedFootprintButNotAtOuterBoundary() {
        assertSame(capital, map.cityAt(capital.centerX + CitySite.CORE_HALF_SIZE + 4, capital.centerY));
        assertNull(map.cityAt(capital.centerX + CitySite.FADE_HALF_SIZE, capital.centerY));
    }

    @Test
    void returnsNullForDesertAndOutOfBoundsClicks() {
        assertNull(map.cityAt(0, 0));
        assertNull(map.cityAt(-1, capital.centerY));
        assertNull(map.cityAt(map.width(), capital.centerY));
    }

    @Test
    void choosesStrongestCityWhenFootprintsOverlap() {
        TileMap smallMap = new TileMap(160, 160, 7L);
        CitySite nearCenter = new CitySite(4, 30, CitySite.CityType.WEST, "Near Center");
        CitySite exactCenter = new CitySite(30, 30, CitySite.CityType.CAPITAL, "Exact Center");
        smallMap.addCity(nearCenter);
        smallMap.addCity(exactCenter);

        assertSame(exactCenter, smallMap.cityAt(30, 30));
    }

    private CitySite findCity(CitySite.CityType type) {
        return map.cities().stream()
                .filter(city -> city.type == type)
                .findFirst()
                .orElseThrow();
    }
}
