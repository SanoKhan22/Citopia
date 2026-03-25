package com.citopia.model;

import com.citopia.render.TileMapRenderer;

/**
 * Represents a city in the transport network.
 * Cities are endpoints for routes and generate cargo demand.
 */
public class City {

    private final String name;
    private final float x;
    private final float y;
    private int population;

    public City(String name, float x, float y, int population) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("City name cannot be null or blank");
        }
        if (population < 0) {
            throw new IllegalArgumentException("Population cannot be negative");
        }
        this.name = name;
        this.x = x;
        this.y = y;
        this.population = population;
    }

    public String getName() { return name; }
    public float getX() { return x; }
    public float getY() { return y; }
    public int getPopulation() { return population; }

    public int getTileX() {
        return (int) (x / TileMapRenderer.TILE_SIZE);
    }

    public int getTileY() {
        return (int) (y / TileMapRenderer.TILE_SIZE);
    }

    public void setPopulation(int population) {
        if (population < 0) {
            throw new IllegalArgumentException("Population cannot be negative");
        }
        this.population = population;
    }

    /**
     * Calculates the Euclidean distance to another city.
     */
    public float distanceTo(City other) {
        float dx = this.x - other.x;
        float dy = this.y - other.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public String toString() {
        return String.format("City{name='%s', pos=(%.1f, %.1f), pop=%d}", name, x, y, population);
    }
}
