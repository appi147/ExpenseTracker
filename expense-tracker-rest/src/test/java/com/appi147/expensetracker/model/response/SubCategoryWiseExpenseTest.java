package com.appi147.expensetracker.model.response;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

class SubCategoryWiseExpenseTest {
    @Test
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(SubCategoryWiseExpense.class)
                .withNonnullFields("subCategory", "amount")
                .suppress(Warning.BIGDECIMAL_EQUALITY)
                .verify();
    }
}