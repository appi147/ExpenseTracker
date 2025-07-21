package com.appi147.expensetracker.model.response;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

class MonthlyExpenseInsightTest {
    @Test
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(MonthlyExpenseInsight.class)
                .withNonnullFields("monthlyBudget", "totalExpense", "categoryWiseExpenses")
                .suppress(Warning.BIGDECIMAL_EQUALITY)
                .verify();
    }
}
