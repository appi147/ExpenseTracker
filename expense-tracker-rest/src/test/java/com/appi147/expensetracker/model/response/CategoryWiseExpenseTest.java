package com.appi147.expensetracker.model.response;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

class CategoryWiseExpenseTest {
    @Test
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(CategoryWiseExpense.class)
                .withNonnullFields("category", "amount", "subCategoryWiseExpenses")
                .suppress(Warning.BIGDECIMAL_EQUALITY)
                .verify();
    }
}
