package com.citopia.world;

/**
 * Describes the terrain/zone role of a single map tile.
 * Computed once by MapGenerator at startup and stored in TileMap.
 */
public enum ZoneType {
    DESERT,
    CITY,           // Capital or outer city inhabitable ground
    OASIS_LAKE,
    OASIS_STONE,    // Rocky shoreline ring around the lake
    OASIS_TREE,     // Tree tiles in the oasis band
    OASIS_GREENERY, // Low shrubs / greenery ring
    LAKE_COAST_DECOR, // Special decor spots on lake edge
    ROAD            // Player-placed road tile
}
