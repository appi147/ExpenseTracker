package com.appi147.expensetracker.controller;

import com.appi147.expensetracker.entity.PaymentType;
import com.appi147.expensetracker.model.request.PaymentTypeRequest;
import com.appi147.expensetracker.service.PaymentTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing PaymentType entities.
 * Provides endpoints for CRUD operations on PaymentType.
 */
@RestController
@RequestMapping("/api/payment-types")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "PaymentType", description = "PaymentType-related operations")
public class PaymentTypeController {

    private final PaymentTypeService paymentTypeService;

    /**
     * Get all payment types.
     *
     * @return a list of all PaymentType objects
     */
    @GetMapping
    @Operation(summary = "Get all payment types", description = "Returns a list of all available payment types")
    public List<PaymentType> getAll() {
        return paymentTypeService.getAll();
    }

    /**
     * Create a new payment type.
     *
     * @param request the details of the payment type to create
     * @return the created PaymentType
     */
    @PostMapping
    @Operation(summary = "Create a new payment type", description = "Creates a new payment type based on the request data")
    public ResponseEntity<PaymentType> create(@RequestBody PaymentTypeRequest request) {
        return ResponseEntity.ok(paymentTypeService.create(request));
    }

    /**
     * Update an existing payment type.
     *
     * @param id      the ID of the payment type to update
     * @param request the updated payment type data
     * @return the updated PaymentType
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a payment type", description = "Updates the payment type with the given ID")
    public ResponseEntity<PaymentType> update(@PathVariable Long id, @RequestBody PaymentTypeRequest request) {
        return ResponseEntity.ok(paymentTypeService.update(id, request));
    }

    /**
     * Delete a payment type.
     *
     * @param id the ID of the payment type to delete
     * @return no content response
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a payment type", description = "Deletes the payment type with the given ID")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paymentTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
