package com.appi147.expensetracker.model.response;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

class SiteWideInsightTest {
    @Test
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(SiteWideInsight.class)
                .withNonnullFields("totalExpensesAdded")
                .suppress(Warning.BIGDECIMAL_EQUALITY)
                .verify();
    }
}
