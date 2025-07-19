package com.appi147.expensetracker.service;

import com.appi147.expensetracker.auth.UserContext;
import com.appi147.expensetracker.model.response.MonthlyTrendRow;
import com.appi147.expensetracker.model.response.SiteWideInsight;
import com.appi147.expensetracker.projection.MonthlyCategoryWiseExpense;
import com.appi147.expensetracker.repository.ExpenseRepository;
import com.appi147.expensetracker.repository.InsightRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class InsightService {

    private final InsightRepository insightRepository;
    private final ExpenseRepository expenseRepository;

    @Cacheable(cacheNames = "siteWideInsight")
    @PreAuthorize("hasRole('ROLE_SUPER_USER')")
    public SiteWideInsight getInsights() {
        return insightRepository.getSiteWideInsight();
    }

    public List<MonthlyTrendRow> getMonthlyTrends() {
        String userId = UserContext.getCurrentUser().getUserId();
        List<MonthlyCategoryWiseExpense> rawData = expenseRepository.getMonthlyCategoryTrends(userId);

        Set<String> allCategories = new TreeSet<>(); // Sorted set of all categories
        Map<String, Map<String, BigDecimal>> grouped = new LinkedHashMap<>();

        for (MonthlyCategoryWiseExpense row : rawData) {
            String month = row.getMonth();
            String category = row.getCategory();
            BigDecimal amount = row.getTotalAmount();

            allCategories.add(category);

            grouped
                    .computeIfAbsent(month, m -> new LinkedHashMap<>())
                    .put(category, amount);
        }

        List<MonthlyTrendRow> result = new ArrayList<>();
        for (Map.Entry<String, Map<String, BigDecimal>> entry : grouped.entrySet()) {
            String month = entry.getKey();
            Map<String, BigDecimal> categoryAmounts = new LinkedHashMap<>();

            for (String category : allCategories) {
                categoryAmounts.put(category, entry.getValue().getOrDefault(category, BigDecimal.ZERO));
            }

            MonthlyTrendRow dto = MonthlyTrendRow.builder()
                    .month(month)
                    .categoryAmounts(categoryAmounts)
                    .build();

            result.add(dto);
        }
        return result;
    }
}
