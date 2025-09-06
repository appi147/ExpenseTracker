package com.appi147.expensetracker.controller;

import com.appi147.expensetracker.entity.RecurringExpense;
import com.appi147.expensetracker.exception.GlobalExceptionHandler;
import com.appi147.expensetracker.service.RecurringExpenseService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RecurringExpenseController.class)
@Import({RecurringExpenseControllerTest.TestConfig.class, GlobalExceptionHandler.class})
@WithMockUser
class RecurringExpenseControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public RecurringExpenseService recurringExpenseService() {
            return Mockito.mock(RecurringExpenseService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecurringExpenseService recurringExpenseService;

    @Test
    void createRecurringExpense_shouldReturnCreated() throws Exception {
        Mockito.reset(recurringExpenseService);

        mockMvc.perform(post("/recurring-expense/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "amount": 100.50,
                                    "dayOfMonth": 10,
                                    "comments": "Monthly subscription",
                                    "subCategoryId": 1,
                                    "paymentTypeCode": "CARD"
                                }
                                """))
                .andExpect(status().isCreated());

        verify(recurringExpenseService).addRecurringExpense(any());
    }

    @Test
    void deleteRecurringExpense_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/recurring-expense/5").with(csrf()))
                .andExpect(status().isNoContent());

        verify(recurringExpenseService).deleteRecurringExpense(5L);
    }

    @Test
    void listRecurringExpenses_shouldReturnOk() throws Exception {
        RecurringExpense r1 = new RecurringExpense();
        r1.setRecurringExpenseId(1L);
        RecurringExpense r2 = new RecurringExpense();
        r2.setRecurringExpenseId(2L);

        when(recurringExpenseService.listRecurringExpensesForCurrentUser()).thenReturn(List.of(r1, r2));

        mockMvc.perform(get("/recurring-expense/list").with(csrf()))
                .andExpect(status().isOk());
    }
}
