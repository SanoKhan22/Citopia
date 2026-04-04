package com.citopia.transport;

import com.citopia.economy.CompanyFinance;
import com.citopia.economy.EconomyCalculator;
import com.citopia.model.Route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Tick-based simulation of active vehicle trips.
 */
public class SimulationEngine {

    private final CompanyFinance companyFinance;
    private final List<TransportTrip> activeTrips;
    private final List<TransportTrip> completedTrips;
    private final Set<String> assignedVehicleIds;

    public SimulationEngine(CompanyFinance companyFinance) {
        this.companyFinance = Objects.requireNonNull(companyFinance, "Company finance cannot be null");
        this.activeTrips = new ArrayList<>();
        this.completedTrips = new ArrayList<>();
        this.assignedVehicleIds = new HashSet<>();
    }

    public TransportTrip assignTrip(Vehicle vehicle, Route route, int cargoUnits) {
        Objects.requireNonNull(vehicle, "Vehicle cannot be null");
        Objects.requireNonNull(route, "Route cannot be null");

        if (assignedVehicleIds.contains(vehicle.getId())) {
            throw new IllegalStateException("Vehicle is already assigned to an active trip");
        }

        TransportTrip trip = new TransportTrip(vehicle, route, cargoUnits);
        activeTrips.add(trip);
        assignedVehicleIds.add(vehicle.getId());
        return trip;
    }

    public void tick() {
        Iterator<TransportTrip> iterator = activeTrips.iterator();
        while (iterator.hasNext()) {
            TransportTrip trip = iterator.next();

            double operatingCost = EconomyCalculator.calculateOperatingCost(trip.getVehicle().getType(), 1);
            companyFinance.debit(operatingCost);

            trip.advanceOneStep();

            if (trip.isCompleted()) {
                double revenue = EconomyCalculator.calculateDeliveryRevenue(
                    trip.getRoute().getDistance(),
                    trip.getCargoUnits()
                );
                companyFinance.credit(revenue);
                completedTrips.add(trip);
                assignedVehicleIds.remove(trip.getVehicle().getId());
                iterator.remove();
            }
        }
    }

    public List<TransportTrip> getActiveTrips() {
        return Collections.unmodifiableList(activeTrips);
    }

    public List<TransportTrip> getCompletedTrips() {
        return Collections.unmodifiableList(completedTrips);
    }
}
