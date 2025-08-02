package com.appi147.expensetracker.controller;

import com.appi147.expensetracker.exception.GlobalExceptionHandler;
import com.appi147.expensetracker.model.response.MonthlyTrendRow;
import com.appi147.expensetracker.model.response.SiteWideInsight;
import com.appi147.expensetracker.service.InsightService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = InsightController.class)
@Import({InsightControllerTest.TestConfig.class, GlobalExceptionHandler.class})
@WithMockUser
class InsightControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public InsightService insightService() {
            return Mockito.mock(InsightService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InsightService insightService;

    @Test
    void getSiteWideInsight_shouldReturnInsight() throws Exception {
        SiteWideInsight dummy = new SiteWideInsight(
                10L, // totalUsersRegistered
                8L,  // totalUsersAddedExpense
                new BigDecimal("50000.00"), // totalExpensesAdded
                120L, // totalTransactionsAdded
                5L,  // totalCategoriesCreated
                10L  // totalSubCategoriesCreated
        );

        when(insightService.getInsights()).thenReturn(dummy);

        mockMvc.perform(get("/insights/site-wide")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void getMonthlyTrends_shouldReturnList() throws Exception {
        Map<String, BigDecimal> categoryAmounts = Map.of(
                "Food", new BigDecimal("1500.00"),
                "Travel", new BigDecimal("800.00")
        );
        MonthlyTrendRow dummyRow = new MonthlyTrendRow("August 2025", categoryAmounts);

        when(insightService.getMonthlyTrends()).thenReturn(List.of(dummyRow));

        mockMvc.perform(get("/insights/monthly-trends")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

}
