package com.citopia.economy;

/**
 * Stores and updates company money state.
 */
public class CompanyFinance {

    private double balance;

    public CompanyFinance(double startingBalance) {
        if (startingBalance < 0) {
            throw new IllegalArgumentException("Starting balance cannot be negative");
        }
        this.balance = startingBalance;
    }

    public double getBalance() {
        return balance;
    }

    public void debit(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Debit amount cannot be negative");
        }
        if (amount > balance) {
            throw new IllegalStateException("Insufficient funds");
        }
        balance -= amount;
    }

    public void credit(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Credit amount cannot be negative");
        }
        balance += amount;
    }
}
