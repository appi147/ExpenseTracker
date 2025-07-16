package com.appi147.expensetracker.controller;

import com.appi147.expensetracker.model.response.SiteWideInsight;
import com.appi147.expensetracker.service.InsightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for fetching site-wide insights related to expenses.
 * Provides aggregated application usage statistics.
 */
@RestController
@RequestMapping("/insights")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Insights", description = "Aggregated statistics across all users and expenses")
public class InsightController {

    private final InsightService insightService;

    /**
     * Retrieves site-wide insights including total users, expenses, transactions, and category data.
     *
     * @return a summary of site-wide metrics
     */
    @GetMapping("/site-wide")
    @Operation(
            summary = "Get site-wide insights",
            description = "Returns a collection of statistics such as total users, total expenses, number of transactions, categories created, etc."
    )
    @ApiResponse(responseCode = "200", description = "Successfully fetched site-wide insight data")
    public ResponseEntity<SiteWideInsight> getSiteWideInsight() {
        SiteWideInsight insight = insightService.getInsights();
        return ResponseEntity.ok(insight);
    }
}
