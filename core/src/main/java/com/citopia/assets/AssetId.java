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
    TERRAIN_CRACKED_HARDPAN("cracked_hardpan_mud_infertile_drought_zones"),
    PROP_CITY_WALL("ancient_mudbrick_square_wall"),
    PROP_CITY_HOUSE("mudbrick_house"),
    PROP_CITY_MARKET_TENT("market_bazaar_trade_tent"),
    PROP_DATE_PALMS("date_palms_oasis_trees"),
    PROP_TREE_MEDIUM("Top-Down Simple Summer_Prop - Tree Medium");

    private final String regionName;

    AssetId(String regionName) {
        this.regionName = regionName;
    }

    public String regionName() {
        return regionName;
    }
}
