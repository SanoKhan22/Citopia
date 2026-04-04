package com.citopia.transport;

import com.citopia.economy.CompanyFinance;
import com.citopia.map.TileMap;
import com.citopia.map.TileType;
import com.citopia.model.City;
import com.citopia.model.CityPlacementValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VehicleShopTest {

    @Test
    @DisplayName("Purchasing a vehicle deducts money and adds fleet item")
    void testPurchaseVehicle() {
        TileMap map = new TileMap(4, 4, TileType.SAND);
        City city = CityPlacementValidator.createTileAlignedCity("Origin", 1, 1, 2000, map);
        CompanyFinance finance = new CompanyFinance(1000);
        VehicleShop shop = new VehicleShop(finance);

        Vehicle vehicle = shop.purchaseVehicle(VehicleType.DONKEY_CARAVAN, city);

        assertEquals(VehicleType.DONKEY_CARAVAN, vehicle.getType());
        assertEquals(1, shop.getOwnedVehicles().size());
        assertEquals(880, finance.getBalance(), 0.001);
    }

    @Test
    @DisplayName("Purchasing fails when there is not enough money")
    void testPurchaseInsufficientFunds() {
        TileMap map = new TileMap(4, 4, TileType.SAND);
        City city = CityPlacementValidator.createTileAlignedCity("Origin", 1, 1, 2000, map);
        CompanyFinance finance = new CompanyFinance(100);
        VehicleShop shop = new VehicleShop(finance);

        assertThrows(IllegalStateException.class,
            () -> shop.purchaseVehicle(VehicleType.MODERN_TRUCK, city));
    }
}
