package com.citopia.economy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CompanyFinanceTest {

    @Test
    @DisplayName("Debit and credit update company balance")
    void testDebitCredit() {
        CompanyFinance finance = new CompanyFinance(500);
        finance.debit(120);
        finance.credit(45);

        assertEquals(425, finance.getBalance(), 0.001);
    }

    @Test
    @DisplayName("Debiting more than balance throws")
    void testInsufficientFunds() {
        CompanyFinance finance = new CompanyFinance(50);

        assertThrows(IllegalStateException.class, () -> finance.debit(60));
    }
}
