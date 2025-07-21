package com.appi147.expensetracker.model.response;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class MonthlyTrendRowTest {
    @Test
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(MonthlyTrendRow.class)
                .withNonnullFields("month", "categoryAmounts")
                .verify();
    }
}
