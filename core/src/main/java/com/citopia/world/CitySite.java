package com.citopia.world;

/**
 * Represents a city location on the map.
 * The capital and all 4 outer cities are CitySite instances.
 */
public class CitySite {

    public enum CityType {
        CAPITAL,
        NORTH,
        SOUTH,
        EAST,
        WEST
    }

    public final int centerX;
    public final int centerY;
    public final CityType type;
    public final String name;

    /** Half-size of the solid city core (in tiles). */
    public static final int CORE_HALF_SIZE = 20;
    /** Radius at which the city ground fully fades into desert (in tiles). */
    public static final int FADE_HALF_SIZE = 32;

    public CitySite(int centerX, int centerY, CityType type, String name) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.type = type;
        this.name = name;
    }

    /** Returns 1.0 fully inside city, 0.0 fully outside, linear fade in between. */
    public float blendAlpha(int tileX, int tileY) {
        int dist = Math.max(Math.abs(tileX - centerX), Math.abs(tileY - centerY));
        if (dist <= CORE_HALF_SIZE) return 1f;
        if (dist >= FADE_HALF_SIZE) return 0f;
        float t = (dist - CORE_HALF_SIZE) / (float) (FADE_HALF_SIZE - CORE_HALF_SIZE);
        return 1f - t;
    }

    /** Returns true if the tile is within the full fade boundary of this city. */
    public boolean contains(int tileX, int tileY) {
        return blendAlpha(tileX, tileY) > 0f;
    }
}
