package com.citopia.world;

/**
 * Bakes all procedural math into TileMap's zone arrays at world-gen time.
 * The renderer reads pre-computed data instead of calling math per-frame.
 *
 * Oasis constants match the original GameScreen values exactly:
 *   OASIS_HALF_WIDTH = 6, OASIS_HALF_HEIGHT = 4
 */
public final class MapGenerator {

    // Oasis geometry (must match former GameScreen constants)
    private static final int OASIS_HW = 6;
    private static final int OASIS_HH = 4;

    // Outer city placement: how many tiles away from center
    private static final int OUTER_CITY_DISTANCE = 80;

    private MapGenerator() {}

    // ── Main entry point ──────────────────────────────────────────────────────

    public static void generate(TileMap map, long seed) {
        int midX = map.width()  / 2;
        int midY = map.height() / 2;

        // 1. Register capital & 4 outer cities
        registerCities(map, midX, midY);

        // 2. Paint city ground zones (CITY type, with fade)
        paintCityZones(map);

        // 3. Paint central oasis
        paintOasis(map, midX, midY);

        // 4. Lay permanent road network (capital <-> 4 outer cities)
        buildPresetRoadNetwork(map);
    }

    /**
     * Builds the permanent road network in two layers:
     *
     *  1. OUTER RING – 4 L-shaped segments that link adjacent outer cities
     *     through the map quadrants, completely bypassing the capital.
     *     Layout (schematic):
     *
     *          N
     *         /|\
     *        / | \
     *       NW |  NE   <- L-shape through the NW quadrant and NE quadrant
     *      /   |   \
     *     W----+----E   (+ is NOT a road; the capital sits here)
     *      \   |   /
     *       SW |  SE
     *        \ | /
     *          S
     *
     *     Concretely:
     *       N↔E : horizontal along N.cy from N.e_edge to corner,
     *             then vertical  along E.cx from corner to E.n_edge
     *       E↔S : vertical  along E.cx from E.s_edge to corner,
     *             then horizontal along S.cy from corner to S.e_edge
     *       S↔W : horizontal along S.cy from S.w_edge to corner,
     *             then vertical  along W.cx from corner to W.s_edge
     *       W↔N : vertical  along W.cx from W.n_edge to corner,
     *             then horizontal along N.cy from corner to N.w_edge
     *
     *  2. CAPITAL SPURS – 4 single-tile-wide roads that shoot out from
     *     each edge of the capital and terminate at the nearest outer city,
     *     giving the capital 4 separate access points to the network without
     *     roads crossing inside it.
     *
     * All tiles placed via placeRoadPermanent() and are non-demolishable.
     */
    private static void buildPresetRoadNetwork(TileMap map) {
        CitySite cap = findCity(map, CitySite.CityType.CAPITAL);
        CitySite N   = findCity(map, CitySite.CityType.NORTH);
        CitySite S   = findCity(map, CitySite.CityType.SOUTH);
        CitySite E   = findCity(map, CitySite.CityType.EAST);
        CitySite W   = findCity(map, CitySite.CityType.WEST);
        if (cap == null || N == null || S == null || E == null || W == null) return;

        int half = CitySite.CORE_HALF_SIZE;

        // ── Capital spurs (1 tile wide, no roads inside city core) ────────────
        // North spur: cap.N_edge → N city centre (entry point)
        vRoad(map, cap.centerX, cap.centerY + half + 1, N.centerY);
        // South spur: S city centre → cap.S_edge
        vRoad(map, cap.centerX, S.centerY, cap.centerY - half - 1);
        // East spur:  cap.E_edge → E city centre
        hRoad(map, cap.centerX + half + 1, E.centerX, cap.centerY);
        // West spur:  W city centre → cap.W_edge
        hRoad(map, W.centerX, cap.centerX - half - 1, cap.centerY);

        // ── Outer ring: 4 L-shaped segments ──────────────────────────────────
        // NE: horizontal from N.e_edge to (E.cx, N.cy) corner,
        //     then vertical  down to E.n_edge
        hRoad(map, N.centerX + half + 1, E.centerX, N.centerY);
        vRoad(map, E.centerX, E.centerY + half + 1, N.centerY);   // corner point included

        // SE: vertical from E.s_edge to (E.cx, S.cy) corner,
        //     then horizontal west to S.e_edge
        vRoad(map, E.centerX, S.centerY, E.centerY - half - 1);
        hRoad(map, S.centerX + half + 1, E.centerX, S.centerY);   // corner point included

        // SW: horizontal from S.w_edge to (W.cx, S.cy) corner,
        //     then vertical  up to W.s_edge
        hRoad(map, W.centerX, S.centerX - half - 1, S.centerY);
        vRoad(map, W.centerX, S.centerY, W.centerY - half - 1);   // corner point included

        // NW: vertical from W.n_edge to (W.cx, N.cy) corner,
        //     then horizontal east to N.w_edge
        vRoad(map, W.centerX, W.centerY + half + 1, N.centerY);
        hRoad(map, W.centerX, N.centerX - half - 1, N.centerY);   // corner point included
    }

    /** Draw a horizontal 1-tile-wide permanent road from x1 to x2 at fixed y. */
    private static void hRoad(TileMap map, int x1, int x2, int y) {
        int lo = Math.min(x1, x2), hi = Math.max(x1, x2);
        for (int x = lo; x <= hi; x++) map.placeRoadPermanent(x, y);
    }

    /** Draw a vertical 1-tile-wide permanent road from y1 to y2 at fixed x. */
    private static void vRoad(TileMap map, int x, int y1, int y2) {
        int lo = Math.min(y1, y2), hi = Math.max(y1, y2);
        for (int y = lo; y <= hi; y++) map.placeRoadPermanent(x, y);
    }

    /** Find a city by type; returns null if not registered. */
    private static CitySite findCity(TileMap map, CitySite.CityType type) {
        return map.cities().stream()
                .filter(c -> c.type == type)
                .findFirst().orElse(null);
    }


    // ── City registration ─────────────────────────────────────────────────────

    private static void registerCities(TileMap map, int midX, int midY) {
        int d = OUTER_CITY_DISTANCE;
        int cap = CitySite.CORE_HALF_SIZE; // ensure we stay in bounds
        int w = map.width();
        int h = map.height();

        map.addCity(new CitySite(midX, midY, CitySite.CityType.CAPITAL, "Capital"));

        // Clamp outer cities inside map bounds
        map.addCity(new CitySite(
                midX, Math.min(midY + d, h - cap - 1), CitySite.CityType.NORTH, "North City"));
        map.addCity(new CitySite(
                midX, Math.max(midY - d, cap + 1), CitySite.CityType.SOUTH, "South City"));
        map.addCity(new CitySite(
                Math.min(midX + d, w - cap - 1), midY, CitySite.CityType.EAST, "East City"));
        map.addCity(new CitySite(
                Math.max(midX - d, cap + 1), midY, CitySite.CityType.WEST, "West City"));
    }

    // ── City ground zone painting ─────────────────────────────────────────────

    private static void paintCityZones(TileMap map) {
        for (CitySite city : map.cities()) {
            int x0 = Math.max(0, city.centerX - CitySite.FADE_HALF_SIZE);
            int x1 = Math.min(map.width()  - 1, city.centerX + CitySite.FADE_HALF_SIZE);
            int y0 = Math.max(0, city.centerY - CitySite.FADE_HALF_SIZE);
            int y1 = Math.min(map.height() - 1, city.centerY + CitySite.FADE_HALF_SIZE);

            for (int y = y0; y <= y1; y++) {
                for (int x = x0; x <= x1; x++) {
                    if (city.blendAlpha(x, y) > 0f) {
                        // Don't overwrite oasis tiles (painted later for capital)
                        if (map.zone(x, y) == ZoneType.DESERT) {
                            map.setZone(x, y, ZoneType.CITY);
                        }
                    }
                }
            }
        }
    }

    // ── Oasis painting (capital only) ─────────────────────────────────────────

    private static void paintOasis(TileMap map, int midX, int midY) {
        int searchR = OASIS_HW + 10; // generous bounding box for oasis + tree ring
        int x0 = Math.max(0, midX - searchR);
        int x1 = Math.min(map.width()  - 1, midX + searchR);
        int y0 = Math.max(0, midY - searchR);
        int y1 = Math.min(map.height() - 1, midY + searchR);

        for (int y = y0; y <= y1; y++) {
            for (int x = x0; x <= x1; x++) {
                ZoneType zone = classifyOasisTile(x, y, midX, midY);
                if (zone != ZoneType.DESERT) {
                    map.setZone(x, y, zone);
                }
            }
        }
    }

    /**
     * Replicates the original GameScreen priority chain exactly:
     *   lake → stone → coast_decor → tree → greenery → (city/desert from outer)
     */
    static ZoneType classifyOasisTile(int x, int y, int midX, int midY) {
        if (isLake(x, y, midX, midY))           return ZoneType.OASIS_LAKE;
        if (isCoastDecor(x, y, midX, midY))      return ZoneType.LAKE_COAST_DECOR;
        if (isStone(x, y, midX, midY))           return ZoneType.OASIS_STONE;
        if (isTree(x, y, midX, midY))            return ZoneType.OASIS_TREE;
        if (isGreenery(x, y, midX, midY))        return ZoneType.OASIS_GREENERY;
        return ZoneType.DESERT;
    }

    // ── Geometry helpers (ported from GameScreen verbatim) ────────────────────

    static boolean isLake(int x, int y, int midX, int midY) {
        float nx = (x - midX) / (float) OASIS_HW;
        float ny = (y - midY) / (float) OASIS_HH;
        return nx * nx + ny * ny <= 1f;
    }

    private static boolean isCoastDecor(int x, int y, int midX, int midY) {
        return (x == midX - 2 && y == midY + OASIS_HH + 1)
            || (x == midX + 2 && y == midY + OASIS_HH + 1)
            || (x == midX     && y == midY - OASIS_HH - 1);
    }

    private static boolean isStone(int x, int y, int midX, int midY) {
        if (isLake(x, y, midX, midY)) return false;
        if (isCoastDecor(x, y, midX, midY)) return false;

        float nxO = (x - midX) / (float) (OASIS_HW + 2);
        float nyO = (y - midY) / (float) (OASIS_HH + 2);
        float nxI = (x - midX) / (float) (OASIS_HW + 1);
        float nyI = (y - midY) / (float) (OASIS_HH + 1);

        boolean nearBand = (nxO*nxO + nyO*nyO <= 1f) && (nxI*nxI + nyI*nyI > 1f);
        if (!nearBand) return false;

        int noise = Math.floorMod((x * 43) ^ (y * 71), 100);
        return noise < 55;
    }

    private static boolean isExtraTree(int x, int y, int midX, int midY) {
        return (x == midX - (OASIS_HW + 3) && y == midY + 1)
            || (x == midX + (OASIS_HW + 3) && y == midY)
            || (x == midX                  && y == midY - (OASIS_HH + 4));
    }

    private static boolean nearExtraTree(int x, int y, int midX, int midY) {
        for (int oy = -2; oy <= 2; oy++)
            for (int ox = -2; ox <= 2; ox++)
                if (isExtraTree(x + ox, y + oy, midX, midY)) return true;
        return false;
    }

    private static boolean isTree(int x, int y, int midX, int midY) {
        if (isLake(x, y, midX, midY)) return false;
        if (isCoastDecor(x, y, midX, midY)) return false;
        if (isStone(x, y, midX, midY)) return false;
        if (isExtraTree(x, y, midX, midY)) return true; // extra lake trees
        if (nearExtraTree(x, y, midX, midY)) return false;

        float nxO = (x - midX) / (float) (OASIS_HW + 3);
        float nyO = (y - midY) / (float) (OASIS_HH + 3);
        float nxI = (x - midX) / (float) (OASIS_HW + 1);
        float nyI = (y - midY) / (float) (OASIS_HH + 1);

        boolean nearBand = (nxO*nxO + nyO*nyO <= 1f) && (nxI*nxI + nyI*nyI > 1f);
        if (!nearBand) return false;

        int noise = Math.floorMod((x * 79) ^ (y * 41), 100);
        return noise < 16;
    }

    private static boolean nearAnyTree(int x, int y, int midX, int midY) {
        for (int oy = -1; oy <= 1; oy++)
            for (int ox = -1; ox <= 1; ox++)
                if (isTree(x + ox, y + oy, midX, midY)) return true;
        return false;
    }

    private static boolean isGreenery(int x, int y, int midX, int midY) {
        if (isLake(x, y, midX, midY)) return false;
        if (isCoastDecor(x, y, midX, midY)) return false;
        if (isStone(x, y, midX, midY)) return false;
        if (isTree(x, y, midX, midY)) return false;
        if (nearAnyTree(x, y, midX, midY)) return false;

        float nxO = (x - midX) / (float) (OASIS_HW + 5);
        float nyO = (y - midY) / (float) (OASIS_HH + 5);
        float nxI = (x - midX) / (float) (OASIS_HW + 2);
        float nyI = (y - midY) / (float) (OASIS_HH + 2);

        boolean inBand = (nxO*nxO + nyO*nyO <= 1f) && !(nxI*nxI + nyI*nyI <= 1f);
        if (!inBand) return false;

        int noise = Math.floorMod((x * 37) ^ (y * 97), 100);
        return noise < 34;
    }
}
