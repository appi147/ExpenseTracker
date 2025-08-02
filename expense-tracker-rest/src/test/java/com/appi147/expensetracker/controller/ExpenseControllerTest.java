package com.appi147.expensetracker.controller;

import com.appi147.expensetracker.entity.Expense;
import com.appi147.expensetracker.exception.GlobalExceptionHandler;
import com.appi147.expensetracker.model.response.CategoryWiseExpense;
import com.appi147.expensetracker.model.response.MonthlyExpense;
import com.appi147.expensetracker.model.response.MonthlyExpenseInsight;
import com.appi147.expensetracker.model.response.SubCategoryWiseExpense;
import com.appi147.expensetracker.service.ExpenseService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ExpenseController.class)
@Import({ExpenseControllerTest.TestConfig.class, GlobalExceptionHandler.class})
@WithMockUser
class ExpenseControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ExpenseService expenseService() {
            return Mockito.mock(ExpenseService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExpenseService expenseService;

    @Test
    void createExpense_shouldReturnCreated() throws Exception {
        Mockito.reset(expenseService);
        mockMvc.perform(post("/expense/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "amount": 100.50,
                                    "date": "2024-08-01",
                                    "comments": "Dinner",
                                    "subCategoryId": 1,
                                    "paymentTypeCode": "CARD",
                                    "monthsToAmortize": 1
                                }
                                """))
                .andExpect(status().isCreated());

        verify(expenseService).addExpense(any());
    }

    @Test
    void createExpense_shouldReturnCreated_Year() throws Exception {
        Mockito.reset(expenseService);
        mockMvc.perform(post("/expense/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "amount": 100.50,
                                    "date": "2024-08-01",
                                    "comments": "Dinner",
                                    "subCategoryId": 1,
                                    "paymentTypeCode": "CARD",
                                    "monthsToAmortize": 12
                                }
                                """))
                .andExpect(status().isCreated());

        verify(expenseService).addExpense(any());
    }

    @Test
    void getMonthlyExpense_shouldReturnData() throws Exception {
        MonthlyExpense dummy = new MonthlyExpense(new BigDecimal("123.45"), new BigDecimal("123.45"));
        when(expenseService.getCurrentMonthExpense()).thenReturn(dummy);

        mockMvc.perform(get("/expense/monthly").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void getMonthlyInsight_shouldReturnData() throws Exception {
        // Mock sub-category level data
        List<SubCategoryWiseExpense> subExpenses = List.of(
                new SubCategoryWiseExpense("DINING_OUT", new BigDecimal("1200.00")),
                new SubCategoryWiseExpense("GROCERIES", new BigDecimal("1800.00"))
        );

        // Mock category-wise data including sub-categories
        List<CategoryWiseExpense> categoryExpenses = List.of(
                new CategoryWiseExpense("FOOD", new BigDecimal("3000.00"), subExpenses),
                new CategoryWiseExpense("TRAVEL", new BigDecimal("1500.00"), List.of())
        );

        // Final mock insight object
        MonthlyExpenseInsight mockInsight = new MonthlyExpenseInsight(
                new BigDecimal("10000.00"),
                new BigDecimal("7500.50"),
                categoryExpenses
        );

        when(expenseService.getMonthlyExpenseInsight(true)).thenReturn(mockInsight);

        mockMvc.perform(get("/expense/insight?monthly=true").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteExpense_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/expense/10").with(csrf()))
                .andExpect(status().isNoContent());

        verify(expenseService).deleteExpense(10L);
    }

    @Test
    void updateExpenseAmount_shouldReturnOk() throws Exception {
        mockMvc.perform(put("/expense/20/amount")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "amount": 200.00 }
                                """))
                .andExpect(status().isOk());

        verify(expenseService).updateExpenseAmount(20L, new BigDecimal("200.00"));
    }

    @Test
    void getExpenses_shouldReturnPaged() throws Exception {
        Page<Expense> mockPage = new PageImpl<>(List.of(new Expense()));
        when(expenseService.getFilteredExpenses(
                any(), any(), any(), any(), any(), anyInt(), anyInt()
        )).thenReturn(mockPage);

        mockMvc.perform(get("/expense/list?page=0&size=5").with(csrf()))
                .andExpect(status().isOk());
    }
}
