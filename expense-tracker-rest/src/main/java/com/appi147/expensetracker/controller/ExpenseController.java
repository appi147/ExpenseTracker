package com.appi147.expensetracker.controller;

import com.appi147.expensetracker.entity.Expense;
import com.appi147.expensetracker.model.request.CreateExpenseRequest;
import com.appi147.expensetracker.model.request.EditExpenseAmount;
import com.appi147.expensetracker.model.response.MonthlyExpense;
import com.appi147.expensetracker.model.response.MonthlyExpenseInsight;
import com.appi147.expensetracker.model.response.PagedResponse;
import com.appi147.expensetracker.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expense")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Expense", description = "Expense-related operations")
public class ExpenseController {

    private final ExpenseService expenseService;

    /**
     * Returns total and category-wise expense for the current month.
     */
    @GetMapping("/monthly")
    @Operation(summary = "Get current month's total expense")
    public MonthlyExpense getMonthlyExpense() {
        return expenseService.getCurrentMonthExpense();
    }

    /**
     * Returns detailed category-wise and sub-category-wise expense insights for the current month or last 30 days.
     *
     * @param monthly true to fetch insights for the current month, false for last 30 days.
     */
    @GetMapping("/insight")
    @Operation(summary = "Get category-wise and sub-category-wise monthly expense insights",
            description = "Returns expenses grouped by category and sub-category, with total amounts.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully fetched expense insight")
            })
    public ResponseEntity<MonthlyExpenseInsight> getMonthlyInsight(
            @RequestParam(defaultValue = "true") @Parameter(description = "Set to false to get last 30 days instead of current month") boolean monthly) {
        MonthlyExpenseInsight insight = expenseService.getMonthlyExpenseInsight(monthly);
        return ResponseEntity.ok(insight);
    }

    /**
     * Creates a new expense.
     */
    @PostMapping("/create")
    @Operation(summary = "Create a new expense")
    public ResponseEntity<Void> createExpense(@Valid @RequestBody CreateExpenseRequest request) {
        expenseService.addExpense(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Returns a paginated and optionally filtered list of expenses.
     */
    @GetMapping("/list")
    @Operation(summary = "List expenses with optional filters and pagination")
    public ResponseEntity<PagedResponse<Expense>> getExpenses(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long subCategoryId,
            @RequestParam(required = false) String paymentTypeCode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Expense> filteredExpenses = expenseService.getFilteredExpenses(categoryId, subCategoryId, paymentTypeCode, dateFrom, dateTo, page, size);
        PagedResponse<Expense> response = new PagedResponse<>(
                filteredExpenses.getContent(),
                filteredExpenses.getNumber(),
                filteredExpenses.getSize(),
                filteredExpenses.getTotalElements(),
                filteredExpenses.getTotalPages(),
                filteredExpenses.isLast()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a specific expense by ID.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an expense",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Expense deleted"),
                    @ApiResponse(responseCode = "404", description = "Expense not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            })
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Updates the amount for a specific expense.
     */
    @PutMapping("/{expenseId}/amount")
    @Operation(summary = "Update the amount of an expense")
    public ResponseEntity<Void> updateExpenseAmount(@PathVariable Long expenseId, @Valid @RequestBody EditExpenseAmount editExpenseAmount) {
        expenseService.updateExpenseAmount(expenseId, editExpenseAmount.getAmount());
        return ResponseEntity.ok().build();
    }
}
