package com.appi147.expensetracker.model.response;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

class MonthlyExpenseTest {
    @Test
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(MonthlyExpense.class)
                .withNonnullFields("last30Days", "currentMonth")
                .suppress(Warning.BIGDECIMAL_EQUALITY)
                .verify();
    }
}
