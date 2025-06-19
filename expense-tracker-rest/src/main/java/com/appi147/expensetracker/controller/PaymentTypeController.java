package com.appi147.expensetracker.controller;

import com.appi147.expensetracker.entity.PaymentType;
import com.appi147.expensetracker.model.request.PaymentTypeRequest;
import com.appi147.expensetracker.service.PaymentTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-types")
@RequiredArgsConstructor
public class PaymentTypeController {

    private final PaymentTypeService paymentTypeService;

    @GetMapping
    public List<PaymentType> getAll() {
        return paymentTypeService.getAll();
    }

    @PostMapping
    public ResponseEntity<PaymentType> create(@RequestBody PaymentTypeRequest request) {
        return ResponseEntity.ok(paymentTypeService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentType> update(@PathVariable Long id, @RequestBody PaymentTypeRequest request) {
        return ResponseEntity.ok(paymentTypeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paymentTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
