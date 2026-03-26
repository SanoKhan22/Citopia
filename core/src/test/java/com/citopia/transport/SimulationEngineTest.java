package com.citopia.transport;

import com.citopia.economy.CompanyFinance;
import com.citopia.map.TileMap;
import com.citopia.map.TileType;
import com.citopia.model.City;
import com.citopia.model.CityPlacementValidator;
import com.citopia.model.Route;
import com.citopia.pathfinding.AStarPathfinder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimulationEngineTest {

    @Test
    @DisplayName("Simulation tick completes trip and updates balance")
    void testTripCompletionAndFinance() {
        TileMap map = new TileMap(8, 8, TileType.SAND);
        City origin = CityPlacementValidator.createTileAlignedCity("A", 1, 1, 1000, map);
        City destination = CityPlacementValidator.createTileAlignedCity("B", 5, 4, 1000, map);

        Route route = new RoutePlanner(new AStarPathfinder()).planRoute(map, origin, destination);

        CompanyFinance finance = new CompanyFinance(2000);
        VehicleShop vehicleShop = new VehicleShop(finance);
        Vehicle vehicle = vehicleShop.purchaseVehicle(VehicleType.CAMEL_TRAIN, origin);

        SimulationEngine engine = new SimulationEngine(finance);
        engine.assignTrip(vehicle, route, 10);

        int requiredTicks = (int) route.getDistance();
        for (int i = 0; i < requiredTicks; i++) {
            engine.tick();
        }

        double expectedBalance = 2000
            - VehicleType.CAMEL_TRAIN.getPurchaseCost()
            - (VehicleType.CAMEL_TRAIN.getOperatingCostPerTick() * requiredTicks)
            + (route.getDistance() * 10 * 4.0);

        assertTrue(engine.getActiveTrips().isEmpty());
        assertEquals(1, engine.getCompletedTrips().size());
        assertEquals(destination, vehicle.getCurrentCity());
        assertEquals(expectedBalance, finance.getBalance(), 0.001);
    }
}
