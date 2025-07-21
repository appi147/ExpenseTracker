package com.appi147.expensetracker.model.response;

import com.appi147.expensetracker.entity.Expense;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

class PagedResponseTest {

    @Test
    void shouldVerifyEqualsAndHashCode() {
        Expense redExpense = new Expense();
        redExpense.setExpenseId(1L);
        redExpense.setAmount(new BigDecimal("100.00"));
        redExpense.setDate(LocalDate.now());
        redExpense.setComments("Red expense");

        Expense blackExpense = new Expense();
        blackExpense.setExpenseId(2L);
        blackExpense.setAmount(new BigDecimal("200.00"));
        blackExpense.setDate(LocalDate.now().minusDays(1));
        blackExpense.setComments("Black expense");

        List<Expense> red = List.of(redExpense);
        List<Expense> black = List.of(blackExpense);

        EqualsVerifier.forClass(PagedResponse.class)
                .withPrefabValues(List.class, red, black)
                .withNonnullFields("content")
                .verify();
    }
}