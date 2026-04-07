package com.citopia.assets;

public enum AssetId {
    VEHICLE_WOODEN_CART("Top-Down Simple Summer_Prop - Wooden Cart"),
    TILE_GROUND_01("Top-Down Simple Summer_Ground 01"),
    TERRAIN_DESERT_SAND("desert_sand_primaryground"),
    TERRAIN_DESERT_STONE_1("desert_stone1"),
    TERRAIN_DESERT_STONE_2("desert_stone2"),
    TERRAIN_DESERT_STONE_3("desert_stone3"),
    TERRAIN_DESERT_STONE_4("desert_stone4"),
    TERRAIN_DESERT_STONE_5("desert_stone5"),
    TERRAIN_OASIS_LAKE("lake_noedge"),
    TERRAIN_CRACKED_HARDPAN("cracked_hardpan_mud_infertile_drought_zones"),
    PROP_CITY_WALL("ancient_mudbrick_square_wall"),
    PROP_CITY_HOUSE("mudbrick_house"),
    PROP_WELL("Top-Down Simple Summer_Prop - Well"),
    PROP_BUILDING_1("building_1"),
    PROP_BUILDING_2("building_2"),
    PROP_BUILDING_3("building_3"),
    PROP_BUILDING_4("building_4"),
    PROP_BUILDING_5("building_5"),
    PROP_CITY_MARKET_TENT("market_bazaar_trade_tent"),
    PROP_DATE_PALMS("date_palms_oasis_trees"),
    PROP_GREENERY_6("greenery_6"),
    PROP_GREENERY_10("greenery_10"),
    PROP_STONES_1("stones_1"),
    PROP_STONES_4("stones_4"),
    PROP_STONES_7("stones_7"),
    PROP_BIGSTONES_BASE_12("bigstones_12 for buildingmountains"),
    PROP_BIGSTONES_MID_11("bigstones_11 for buildingmountains"),
    PROP_BIGSTONES_PEAK_10("bigstones_10 for buildingmountains"),
    PROP_DECOR_3("decor_3"),
    PROP_DECOR_5("decor_5"),
    PROP_TREE_8("tree_8"),
    PROP_TREE_LARGE("Top-Down Simple Summer_prop - Tree Large"),
    PROP_TREE_10("tree_10"),
    PROP_TREE_MEDIUM("Top-Down Simple Summer_Prop - Tree Medium");

    private final String regionName;

    AssetId(String regionName) {
        this.regionName = regionName;
    }

    public String regionName() {
        return regionName;
    }
}
