package com.citopia.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CityBudgetTest {

    private CityBudget budget;

    @BeforeEach
    public void setUp() {
        // Start each test with 1000 coins, max debt allowed is 500
        budget = new CityBudget(1000, 500);
    }

    /**
     * Non-trivial Test 1: Testing tax collection with logic rules.
     * Evaluates happy path (high happiness) vs edge case (happiness penalty threshold).
     */
    @Test
    public void testCollectTaxesWithHappinessPenalty() {
        // Happy citizenry: 100 population, 0.8 happiness -> 100 * 10 * 0.8 * 1.0 = 800
        int collectedHappy = budget.collectTaxes(100, 0.8);
        assertEquals(800, collectedHappy, "Tax yield should be base * happiness");
        assertEquals(1800, budget.getBalance(), "Balance should increase by tax yield");

        // Unhappy citizenry (below 0.3): 100 population, 0.2 happiness -> 100 * 10 * 0.2 * 0.5 (penalty) = 100
        int collectedUnhappy = budget.collectTaxes(100, 0.2);
        assertEquals(100, collectedUnhappy, "Tax yield should be halved if happiness is below 0.3");
        assertEquals(1900, budget.getBalance(), "Balance should cumulatively increase");
    }

    /**
     * Non-trivial Test 2: Simulating purchasing decisions around the debt boundary.
     * Tests hard-to-reach boundaries and logic gating.
     */
    @Test
    public void testBuyBuildingBoundary() {
        // Current balance: 1000. Cost: 1300. 
        // Remaining balance will be -300. Max debt is 500 (meaning allowed to drop to -500).
        boolean firstPurchase = budget.buyBuilding(1300);
        assertTrue(firstPurchase, "Should be allowed to buy by going into permitted debt");
        assertEquals(-300, budget.getBalance(), "Balance correctly reflects the debt");

        // Current balance: -300. Try to buy another thing costing 250.
        // Result would be -550, which is below -500 max debt boundary.
        boolean secondPurchase = budget.buyBuilding(250);
        assertFalse(secondPurchase, "Should reject purchase that exceeds max debt");
        assertEquals(-300, budget.getBalance(), "Balance should remain unaffected after failed purchase");
    }

    /**
     * Non-trivial Test 3: Forcing a bankruptcy state through mandatory maintenance.
     * Tests that mandatory deductions correctly evaluate insolvency.
     */
    @Test
    public void testPayMaintenanceForcesBankruptcy() {
        // Current balance: 1000. Overwhelming unexpected maintenance of 1600.
        // Drops balance to -600 (below -500 max debt).
        boolean isSolvent = budget.payMaintenance(1600);
        
        assertFalse(isSolvent, "City should be flagged as bankrupt if maintenance pushes debt too far.");
        assertEquals(-600, budget.getBalance(), "Balance still drops even when going bankrupt.");
    }

    /**
     * Edge case and Exception testing for constructor and inputs.
     * Covers error paths per Milestone 3 requirements.
     */
    @Test
    public void testInvalidInputsThrowExceptions() {
        // Constructor errors
        assertThrows(IllegalArgumentException.class, () -> new CityBudget(-600, 500), 
            "Initial balance below debt limit should throw");
        assertThrows(IllegalArgumentException.class, () -> new CityBudget(1000, -100), 
            "Negative max debt should throw");

        // Tax collection errors
        assertThrows(IllegalArgumentException.class, () -> budget.collectTaxes(-50, 0.8), 
            "Negative population should throw");
        assertThrows(IllegalArgumentException.class, () -> budget.collectTaxes(100, 1.5), 
            "Happiness > 1.0 should throw");
        assertThrows(IllegalArgumentException.class, () -> budget.collectTaxes(100, -0.1), 
            "Happiness < 0.0 should throw");

        // Transaction errors
        assertThrows(IllegalArgumentException.class, () -> budget.buyBuilding(-100), 
            "Negative purchase cost should throw");
        assertThrows(IllegalArgumentException.class, () -> budget.payMaintenance(-50), 
            "Negative maintenance cost should throw");
    }
}