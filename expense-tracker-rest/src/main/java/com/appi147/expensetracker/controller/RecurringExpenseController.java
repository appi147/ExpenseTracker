package com.appi147.expensetracker.controller;

import com.appi147.expensetracker.entity.RecurringExpense;
import com.appi147.expensetracker.model.request.RecurringExpenseCreateRequest;
import com.appi147.expensetracker.service.RecurringExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recurring-expense")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "RecurringExpense", description = "Recurring expense-related operations")
public class RecurringExpenseController {

    private final RecurringExpenseService recurringExpenseService;

    /**
     * Creates a new recurring expense.
     */
    @PostMapping("/create")
    @Operation(summary = "Create a new recurring expense",
            description = "Adds a recurring expense for the current user. Day of month must be between 1 and 28.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Recurring expense created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request data")
            })
    public ResponseEntity<Void> createRecurringExpense(@Valid @RequestBody RecurringExpenseCreateRequest request) {
        recurringExpenseService.addRecurringExpense(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Deletes a recurring expense by ID.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a recurring expense",
            description = "Deletes a recurring expense by its ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Recurring expense deleted"),
                    @ApiResponse(responseCode = "404", description = "Recurring expense not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            })
    public ResponseEntity<Void> deleteRecurringExpense(@PathVariable Long id) {
        recurringExpenseService.deleteRecurringExpense(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Lists all recurring expenses for the current user.
     */
    @GetMapping("/list")
    @Operation(summary = "List all recurring expenses for the current user",
            description = "Returns all recurring expenses currently present for the authenticated user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully fetched recurring expenses")
            })
    public ResponseEntity<List<RecurringExpense>> listRecurringExpenses() {
        List<RecurringExpense> recurringExpenses = recurringExpenseService.listRecurringExpensesForCurrentUser();
        return ResponseEntity.ok(recurringExpenses);
    }
}
