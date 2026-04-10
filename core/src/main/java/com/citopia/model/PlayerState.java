package com.citopia.model;

/**
 * Issue #24 – PlayerState (Economy)
 *
 * Single source of truth for the player's in-game economy.
 * Wraps CityBudget for financial logic and exposes build costs for all
 * placeable items so the build menu and road-placement checks always
 * read from one place.
 *
 * Road costs match an OpenTTD-inspired scale:
 *   - Basic road tile   : 100
 *   - Maintain / year   : 5 per tile (future use)
 */
public class PlayerState {

    // ── Build costs (gold) ────────────────────────────────────────────────────
    public static final int COST_ROAD        = 100;
    public static final int COST_STATION     = 500;
    public static final int COST_DEMOLISH    = 50;

    // ── Starting economy ──────────────────────────────────────────────────────
    private static final int STARTING_GOLD   = 25_000;
    private static final int MAX_DEBT        = 10_000;  // how far below 0 we allow

    // Year counter (each year: taxes collected, maintenance charged)
    private int year   = 1;
    private int month  = 1;   // 1-12, cosmetic for now

    private final CityBudget budget;

    public PlayerState() {
        this.budget = new CityBudget(STARTING_GOLD, MAX_DEBT);
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public int getGold()   { return budget.getBalance(); }
    public int getYear()   { return year; }
    public int getMonth()  { return month; }

    /** True if the player can afford a purchase of {@code cost} gold. */
    public boolean canAfford(int cost) {
        return budget.getBalance() - cost >= -budget.getMaxDebt();
    }

    // ── Transactions ──────────────────────────────────────────────────────────

    /**
     * Attempt to spend {@code cost} gold. Returns true on success.
     * Use this for any player-initiated build/buy action.
     */
    public boolean spend(int cost) {
        return budget.buyBuilding(cost);
    }

    /**
     * Add gold to the player's balance (trade income, tax yield, etc.).
     */
    public void earn(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Earn amount must be positive");
        budget.collectTaxes(0, 0);   // zero-pop call just to satisfy interface
        // Direct credit — bypass the population-based formula
        budgetCredit(amount);
    }

    // ── Year/Month tick (called by game loop once per in-game period) ─────────

    /**
     * Advance the calendar by one month. When a full year passes, collect
     * taxes from all active cities (stub – population = 0 until #28 cargo system).
     * Returns true if player is solvent, false if bankrupt.
     */
    public boolean tick(int activeCityCount) {
        month++;
        if (month > 12) {
            month = 1;
            year++;
            // Tax stub: 200 gold per active city per year
            int taxYield = activeCityCount * 200;
            budgetCredit(taxYield);
            // Maintenance stub: 10 gold per year (roads charged separately in #26)
            budget.payMaintenance(10);
        }
        return getGold() >= -budget.getMaxDebt();
    }

    // ── Formatting helpers (used by HUD) ──────────────────────────────────────

    /** Format gold with thousands separator for clean HUD display. */
    public String formattedGold() {
        int g = getGold();
        boolean negative = g < 0;
        String abs = String.valueOf(Math.abs(g));
        StringBuilder sb = new StringBuilder();
        int start = abs.length() % 3;
        if (start > 0) sb.append(abs, 0, start);
        for (int i = start; i < abs.length(); i += 3) {
            if (sb.length() > 0) sb.append(',');
            sb.append(abs, i, i + 3);
        }
        return (negative ? "-" : "") + sb;
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /** Direct balance credit that bypasses population formula. */
    private void budgetCredit(int amount) {
        // CityBudget has no direct credit method; use zero-maintenance buyBuilding trick:
        // We "buy" a negative-cost item = credit.
        // Safer: use reflection-free approach — just collect taxes with 1 pop at 100% happiness
        // producing exactly `amount` gold. baseTax = 10.0, 1 pop, 1.0 happiness = 10 gold.
        // Divide amount by 10, use pop as multiplier.
        if (amount <= 0) return;
        // Round to nearest 10 for accuracy; remainder added via loop
        int pops = amount / 10;
        int remainder = amount % 10;
        if (pops > 0) budget.collectTaxes(pops, 1.0);
        // Add remainder one gold at a time (cheap for small remainders)
        for (int i = 0; i < remainder; i++) {
            budget.collectTaxes(1, 0.1); // 1 pop * 10 * 0.1 = 1 gold
        }
    }
}
