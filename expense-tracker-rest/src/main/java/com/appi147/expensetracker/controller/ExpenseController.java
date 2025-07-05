package com.appi147.expensetracker.controller;

import com.appi147.expensetracker.entity.Expense;
import com.appi147.expensetracker.model.request.CreateExpenseRequest;
import com.appi147.expensetracker.model.response.MonthlyExpense;
import com.appi147.expensetracker.service.ExpenseService;
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
    public Page<Expense> getExpenses(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long subCategoryId,
            @RequestParam(required = false) String paymentTypeCode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return expenseService.getFilteredExpenses(categoryId, subCategoryId, paymentTypeCode, dateFrom, dateTo, page, size);
    }
}
