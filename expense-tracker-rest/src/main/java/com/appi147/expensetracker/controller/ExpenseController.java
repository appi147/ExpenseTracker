package com.appi147.expensetracker.controller;

import com.appi147.expensetracker.entity.Expense;
import com.appi147.expensetracker.model.request.CreateExpenseRequest;
import com.appi147.expensetracker.model.request.EditExpenseAmount;
import com.appi147.expensetracker.model.response.MonthlyExpense;
import com.appi147.expensetracker.model.response.PagedResponse;
import com.appi147.expensetracker.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@RequestMapping("/api/expense")
@SecurityRequirement(name = "bearerAuth")
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping("/monthly")
    public MonthlyExpense getMonthlyExpense() {
        return expenseService.getCurrentMonthExpense();
    }

    @PostMapping("/create")
    public ResponseEntity<Void> createExpense(@Valid @RequestBody CreateExpenseRequest request) {
        expenseService.addExpense(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("list")
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

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a expense", responses = {
            @ApiResponse(responseCode = "204", description = "Expense deleted"),
            @ApiResponse(responseCode = "404", description = "Expense not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{expenseId}/amount")
    public ResponseEntity<Void> updateExpenseAmount(@PathVariable Long expenseId, @RequestBody EditExpenseAmount editExpenseAmount) {
        expenseService.updateExpenseAmount(expenseId, editExpenseAmount.getAmount());
        return ResponseEntity.ok().build();
    }
}
