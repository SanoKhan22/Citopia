package com.citopia.model;

import com.citopia.map.TileMap;
import com.citopia.map.TileType;
import com.citopia.render.TileMapRenderer;

/**
 * Validates whether a city can be placed on the current map.
 */
public final class CityPlacementValidator {

    private CityPlacementValidator() {
    }

    public static void validatePlacement(City city, TileMap tileMap) {
        int tileX = city.getTileX();
        int tileY = city.getTileY();

        if (tileX < 0 || tileX >= tileMap.getWidth() || tileY < 0 || tileY >= tileMap.getHeight()) {
            throw new IllegalArgumentException("City must be placed within the map bounds");
        }

        TileType tileType = tileMap.getTile(tileX, tileY).getType();
        if (tileType != TileType.SAND && tileType != TileType.OASIS) {
            throw new IllegalArgumentException("City must be placed on a settlement tile");
        }
    }

    public static City createTileAlignedCity(String name, int tileX, int tileY, int population, TileMap tileMap) {
        float x = tileX * TileMapRenderer.TILE_SIZE + TileMapRenderer.TILE_SIZE / 2f;
        float y = tileY * TileMapRenderer.TILE_SIZE + TileMapRenderer.TILE_SIZE / 2f;
        City city = new City(name, x, y, population);
        validatePlacement(city, tileMap);
        return city;
    }
}
