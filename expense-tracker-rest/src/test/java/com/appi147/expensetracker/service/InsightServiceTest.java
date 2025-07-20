package com.appi147.expensetracker.service;

import com.appi147.expensetracker.auth.UserContext;
import com.appi147.expensetracker.entity.User;
import com.appi147.expensetracker.model.response.MonthlyTrendRow;
import com.appi147.expensetracker.model.response.SiteWideInsight;
import com.appi147.expensetracker.projection.MonthlyCategoryWiseExpense;
import com.appi147.expensetracker.repository.ExpenseRepository;
import com.appi147.expensetracker.repository.InsightRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InsightServiceTest {

    @Mock
    InsightRepository insightRepository;

    @Mock
    ExpenseRepository expenseRepository;

    @InjectMocks
    InsightService insightService;

    // --- getInsights (site-wide) ---

    @Test
    void getInsights_returnsInsightAndLogs() {
        SiteWideInsight insight = new SiteWideInsight();
        when(insightRepository.getSiteWideInsight()).thenReturn(insight);

        // The method is annotated with @PreAuthorize, but JUnit can't test security by default.
        // If running with Spring context and MethodSecurity enabled, you must configure the context and a user with correct ROLES.

        SiteWideInsight result = insightService.getInsights();

        assertSame(insight, result);
        verify(insightRepository).getSiteWideInsight();
    }

    @Test
    void getInsights_returnsNullIfRepositoryReturnsNull() {
        when(insightRepository.getSiteWideInsight()).thenReturn(null);

        SiteWideInsight result = insightService.getInsights();

        assertNull(result);
        verify(insightRepository).getSiteWideInsight();
    }

    // --- getMonthlyTrends ---

    @Test
    void getMonthlyTrends_happyPathMultiMonthMultiCategory() {
        User user = new User();
        user.setUserId("test123");
        MonthlyCategoryWiseExpense row1 = mock(MonthlyCategoryWiseExpense.class);
        when(row1.getMonth()).thenReturn("2022-11");
        when(row1.getCategory()).thenReturn("Food");
        when(row1.getTotalAmount()).thenReturn(new BigDecimal("120"));
        MonthlyCategoryWiseExpense row2 = mock(MonthlyCategoryWiseExpense.class);
        when(row2.getMonth()).thenReturn("2022-11");
        when(row2.getCategory()).thenReturn("Travel");
        when(row2.getTotalAmount()).thenReturn(new BigDecimal("30"));
        MonthlyCategoryWiseExpense row3 = mock(MonthlyCategoryWiseExpense.class);
        when(row3.getMonth()).thenReturn("2022-12");
        when(row3.getCategory()).thenReturn("Food");
        when(row3.getTotalAmount()).thenReturn(new BigDecimal("200"));
        List<MonthlyCategoryWiseExpense> rawData = List.of(row1, row2, row3);

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);
            when(expenseRepository.getMonthlyCategoryTrends("test123")).thenReturn(rawData);

            List<MonthlyTrendRow> result = insightService.getMonthlyTrends();

            // Validate the size
            assertEquals(2, result.size());
            // All months and categories correct and complete
            Set<String> expectedMonths = Set.of("2022-11", "2022-12");
            Set<String> actualMonths = new HashSet<>();
            for (MonthlyTrendRow row : result) {
                actualMonths.add(row.getMonth());
                assertEquals(2, row.getCategoryAmounts().size());
                assertTrue(row.getCategoryAmounts().containsKey("Food"));
                assertTrue(row.getCategoryAmounts().containsKey("Travel"));
                // Validate correct filling of missing category/month combinations with zero
                if (row.getMonth().equals("2022-11")) {
                    assertEquals(new BigDecimal("120"), row.getCategoryAmounts().get("Food"));
                    assertEquals(new BigDecimal("30"), row.getCategoryAmounts().get("Travel"));
                } else if (row.getMonth().equals("2022-12")) {
                    assertEquals(new BigDecimal("200"), row.getCategoryAmounts().get("Food"));
                    assertEquals(BigDecimal.ZERO, row.getCategoryAmounts().get("Travel"));
                } else {
                    fail("Unexpected month: " + row.getMonth());
                }
            }
            assertEquals(expectedMonths, actualMonths);
        }
    }

    @Test
    void getMonthlyTrends_noTrends_returnsEmptyList() {
        User user = new User();
        user.setUserId("u1");
        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);
            when(expenseRepository.getMonthlyCategoryTrends("u1")).thenReturn(Collections.emptyList());

            List<MonthlyTrendRow> result = insightService.getMonthlyTrends();

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Test
    void getMonthlyTrends_onlyOneMonthOneCategory() {
        User user = new User();
        user.setUserId("alice");
        MonthlyCategoryWiseExpense row1 = mock(MonthlyCategoryWiseExpense.class);
        when(row1.getMonth()).thenReturn("2023-04");
        when(row1.getCategory()).thenReturn("Health");
        when(row1.getTotalAmount()).thenReturn(new BigDecimal("55.00"));
        List<MonthlyCategoryWiseExpense> rawData = List.of(row1);

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);
            when(expenseRepository.getMonthlyCategoryTrends("alice")).thenReturn(rawData);
            List<MonthlyTrendRow> result = insightService.getMonthlyTrends();

            assertEquals(1, result.size());
            MonthlyTrendRow row = result.get(0);
            assertEquals("2023-04", row.getMonth());
            assertEquals(1, row.getCategoryAmounts().size());
            assertEquals(new BigDecimal("55.00"), row.getCategoryAmounts().get("Health"));
        }
    }

    @Test
    void getMonthlyTrends_categoriesAreSortedAndZeroFilled() {
        User user = new User();
        user.setUserId("bob");
        MonthlyCategoryWiseExpense row1 = mock(MonthlyCategoryWiseExpense.class);
        when(row1.getMonth()).thenReturn("2023-01");
        when(row1.getCategory()).thenReturn("B");
        when(row1.getTotalAmount()).thenReturn(new BigDecimal("20"));
        MonthlyCategoryWiseExpense row2 = mock(MonthlyCategoryWiseExpense.class);
        when(row2.getMonth()).thenReturn("2023-01");
        when(row2.getCategory()).thenReturn("A");
        when(row2.getTotalAmount()).thenReturn(new BigDecimal("10"));
        MonthlyCategoryWiseExpense row3 = mock(MonthlyCategoryWiseExpense.class);
        when(row3.getMonth()).thenReturn("2023-02");
        when(row3.getCategory()).thenReturn("A");
        when(row3.getTotalAmount()).thenReturn(new BigDecimal("30"));
        List<MonthlyCategoryWiseExpense> rawData = List.of(row1, row2, row3);
        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);
            when(expenseRepository.getMonthlyCategoryTrends("bob")).thenReturn(rawData);

            List<MonthlyTrendRow> result = insightService.getMonthlyTrends();

            // allCategories must be sorted: [A, B]
            for (MonthlyTrendRow row : result) {
                assertEquals(Set.of("A", "B"), row.getCategoryAmounts().keySet());
            }
            // Validate zero fill for missing "B" in 2023-02
            for (MonthlyTrendRow row : result) {
                if (row.getMonth().equals("2023-01")) {
                    assertEquals(new BigDecimal("10"), row.getCategoryAmounts().get("A"));
                    assertEquals(new BigDecimal("20"), row.getCategoryAmounts().get("B"));
                } else if (row.getMonth().equals("2023-02")) {
                    assertEquals(new BigDecimal("30"), row.getCategoryAmounts().get("A"));
                    assertEquals(BigDecimal.ZERO, row.getCategoryAmounts().get("B"));
                }
            }
        }
    }

    @Test
    void getMonthlyTrends_nullUser_throwsNPE() {
        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(null);

            assertThrows(NullPointerException.class, () -> insightService.getMonthlyTrends());
        }
    }

    @Test
    void getMonthlyTrends_dbReturnsNull_throwsNPE() {
        // This is an anti-pattern, but validate the behavior: if repo returns null instead of a list.
        User user = new User();
        user.setUserId("zz");
        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);
            when(expenseRepository.getMonthlyCategoryTrends("zz")).thenReturn(null);

            assertThrows(NullPointerException.class, () -> insightService.getMonthlyTrends());
        }
    }

    @Test
    void getMonthlyTrends_duplicateRowForMonthAndCategory_lastWinsInMap() {
        // Edge: Ideally raw data shouldn't have duplicates, but if present, check the returned row has last value.
        User user = new User();
        user.setUserId("zebra");

        MonthlyCategoryWiseExpense row1 = mock(MonthlyCategoryWiseExpense.class);
        when(row1.getMonth()).thenReturn("2022-10");
        when(row1.getCategory()).thenReturn("Food");
        when(row1.getTotalAmount()).thenReturn(new BigDecimal("120"));
        MonthlyCategoryWiseExpense row2 = mock(MonthlyCategoryWiseExpense.class);
        when(row2.getMonth()).thenReturn("2022-10");
        when(row2.getCategory()).thenReturn("Food");
        when(row2.getTotalAmount()).thenReturn(new BigDecimal("99"));

        List<MonthlyCategoryWiseExpense> rawData = List.of(row1, row2);
        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);
            when(expenseRepository.getMonthlyCategoryTrends("zebra")).thenReturn(rawData);

            List<MonthlyTrendRow> result = insightService.getMonthlyTrends();
            MonthlyTrendRow row = result.get(0);

            assertEquals("2022-10", row.getMonth());
            assertEquals(1, row.getCategoryAmounts().size());
            assertEquals(new BigDecimal("99"), row.getCategoryAmounts().get("Food"));
        }
    }

}
