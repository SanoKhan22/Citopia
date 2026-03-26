package com.citopia.transport;

import com.citopia.economy.CompanyFinance;
import com.citopia.model.City;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Handles fleet purchases.
 */
public class VehicleShop {

    private final CompanyFinance companyFinance;
    private final List<Vehicle> ownedVehicles;

    public VehicleShop(CompanyFinance companyFinance) {
        this.companyFinance = Objects.requireNonNull(companyFinance, "Company finance cannot be null");
        this.ownedVehicles = new ArrayList<>();
    }

    public Vehicle purchaseVehicle(VehicleType vehicleType, City initialCity) {
        Objects.requireNonNull(vehicleType, "Vehicle type cannot be null");
        Objects.requireNonNull(initialCity, "Initial city cannot be null");

        companyFinance.debit(vehicleType.getPurchaseCost());
        Vehicle vehicle = new Vehicle(vehicleType, initialCity);
        ownedVehicles.add(vehicle);
        return vehicle;
    }

    public List<Vehicle> getOwnedVehicles() {
        return Collections.unmodifiableList(ownedVehicles);
    }
}
