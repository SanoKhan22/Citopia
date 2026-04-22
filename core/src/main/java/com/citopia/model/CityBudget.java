package com.citopia.model;

/**
 * Manages the city's financial resources, including tax collection,
 * expenses, and handling of debt capabilities.
 */
public class CityBudget {
    private int balance;
    private int maxDebt;

    /**
     * Initializes the city budget.
     * @param initialBalance The starting money.
     * @param maxDebt The maximum debt (positive number) the city can take on before it goes bankrupt.
     */
    public CityBudget(int initialBalance, int maxDebt) {
        if (maxDebt < 0) {
            throw new IllegalArgumentException("Max debt must be a positive number or zero.");
        }
        if (initialBalance < -maxDebt) {
            throw new IllegalArgumentException("Initial balance cannot be below max debt limit.");
        }
        this.balance = initialBalance;
        this.maxDebt = maxDebt;
    }

    public int getBalance() {
        return balance;
    }

    public int getMaxDebt() {
        return maxDebt;
    }

    /**
     * Calculates and collects taxes based on city population and happiness.
     * 
     * @param population Total taxpayers.
     * @param happiness Multiplier between 0.0 (completely unhappy) and 1.0 (ecstatic).
     * @return The total tax collected and added to the balance.
     */
    public int collectTaxes(int population, double happiness) {
        if (population < 0) {
            throw new IllegalArgumentException("Population cannot be negative.");
        }
        if (happiness < 0.0 || happiness > 1.0) {
            throw new IllegalArgumentException("Happiness must be between 0.0 and 1.0.");
        }

        double baseTaxPerCitizen = 10.0;
        
        // Non-trivial logic: Severe penalty to tax collection if citizens are very unhappy
        double moralePenalty = happiness < 0.3 ? 0.5 : 1.0; 
        
        int taxYield = (int) (population * baseTaxPerCitizen * happiness * moralePenalty);
        this.balance += taxYield;
        
        return taxYield;
    }

    /**
     * Attempts to process a major purchase (like a building).
     * 
     * @param cost The cost of the item.
     * @return true if the purchase was successful, false if it exceeds the max allowed debt.
     */
    public boolean buyBuilding(int cost) {
        if (cost < 0) {
            throw new IllegalArgumentException("Cost cannot be negative.");
        }
        
        // Check if buying this would drop balance below negative maxDebt
        if (this.balance - cost < -this.maxDebt) {
            return false;
        }
        
        this.balance -= cost;
        return true;
    }

    /**
     * Deducts recurring maintenance costs. 
     * Maintenance is mandatory and can force the city beyond its max debt into bankruptcy.
     * 
     * @param maintenanceCost The cost to deduct.
     * @return true if the city is still solvent, false if the city has gone bankrupt (exceeded max debt).
     */
    public boolean payMaintenance(int maintenanceCost) {
        if (maintenanceCost < 0) {
            throw new IllegalArgumentException("Maintenance cost cannot be negative.");
        }
        
        this.balance -= maintenanceCost;
        
        // Return solvent status
        return this.balance >= -this.maxDebt;
    }
}
