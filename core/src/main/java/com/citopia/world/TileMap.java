package com.citopia.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TileMap {

    private final int width;
    private final int height;
    private final byte[] groundTileIndices;
    private final ZoneType[] zoneData;        // pre-computed by MapGenerator
    private final boolean[] roadData;          // true = road exists here
    private final boolean[] permanentRoadData; // true = road cannot be demolished
    private final List<CitySite> cities;       // all city sites (capital + outer)

    public TileMap(int width, int height, long seed) {
        this.width = width;
        this.height = height;
        this.groundTileIndices = new byte[width * height];
        this.zoneData = new ZoneType[width * height];
        this.roadData = new boolean[width * height];
        this.permanentRoadData = new boolean[width * height];
        this.cities = new ArrayList<>();

        // Fill ground tile indices with random variants
        Random random = new Random(seed);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                groundTileIndices[index(x, y)] = (byte) random.nextInt(MapConfig.GROUND_TILE_VARIANTS);
                zoneData[index(x, y)] = ZoneType.DESERT; // default
            }
        }

        // Run map generator to fill zone data and city list
        MapGenerator.generate(this, seed);
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public int width()  { return width; }
    public int height() { return height; }

    public int groundTileIndex(int x, int y) {
        return groundTileIndices[index(x, y)] & 0xFF;
    }

    public ZoneType zone(int x, int y) {
        return zoneData[index(x, y)];
    }

    public boolean hasRoad(int x, int y) {
        return roadData[index(x, y)];
    }

    /** Returns true if this tile has a permanent (non-demolishable) road. */
    public boolean isPermanentRoad(int x, int y) {
        return permanentRoadData[index(x, y)];
    }

    public List<CitySite> cities() {
        return Collections.unmodifiableList(cities);
    }

    public CitySite capital() {
        return cities.stream()
                .filter(c -> c.type == CitySite.CityType.CAPITAL)
                .findFirst()
                .orElse(null);
    }

    // ── Mutators (called by MapGenerator and player actions) ──────────────────

    /** Set a zone type during world generation. */
    public void setZone(int x, int y, ZoneType type) {
        if (inBounds(x, y)) zoneData[index(x, y)] = type;
    }

    /** Place a player-built road tile. */
    public void placeRoad(int x, int y) {
        if (inBounds(x, y)) {
            roadData[index(x, y)] = true;
            zoneData[index(x, y)] = ZoneType.ROAD;
        }
    }

    /**
     * Place a permanent road tile (called by MapGenerator for preset network).
     * Permanent roads cannot be demolished by the player.
     */
    public void placeRoadPermanent(int x, int y) {
        if (inBounds(x, y)) {
            roadData[index(x, y)] = true;
            permanentRoadData[index(x, y)] = true;
            zoneData[index(x, y)] = ZoneType.ROAD;
        }
    }

    /** Remove a road tile (demolish). Silently ignored on permanent roads. */
    public void removeRoad(int x, int y) {
        if (inBounds(x, y) && roadData[index(x, y)] && !permanentRoadData[index(x, y)]) {
            roadData[index(x, y)] = false;
            zoneData[index(x, y)] = ZoneType.CITY;
        }
    }


    /** Register a city site so the map is aware of its boundaries. */
    public void addCity(CitySite city) {
        cities.add(city);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    public boolean inBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    private int index(int x, int y) {
        return y * width + x;
    }
}
