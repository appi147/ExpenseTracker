package com.appi147.expensetracker.service;

import com.appi147.expensetracker.auth.UserContext;
import com.appi147.expensetracker.entity.PaymentType;
import com.appi147.expensetracker.exception.ResourceNotFoundException;
import com.appi147.expensetracker.model.request.PaymentTypeRequest;
import com.appi147.expensetracker.repository.PaymentTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PaymentTypeService {

    private final PaymentTypeRepository paymentTypeRepository;

    @Cacheable(value = "paymentTypes", key = "#code")
    public PaymentType getByCode(String code) {
        return paymentTypeRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentType not found for code: " + code));
    }

    @Cacheable(value = "paymentTypes")
    public List<PaymentType> getAll() {
        return paymentTypeRepository.findAll();
    }

    @PreAuthorize("hasRole('ROLE_SUPER_USER')")
    @CacheEvict(value = "paymentTypes", allEntries = true)
    public PaymentType create(PaymentTypeRequest request) {
        PaymentType paymentType = new PaymentType();
        paymentType.setCreatedBy(UserContext.getCurrentUser());
        paymentType.setCode(request.getCode());
        paymentType.setLabel(request.getLabel());
        return paymentTypeRepository.save(paymentType);
    }

    @PreAuthorize("hasRole('ROLE_SUPER_USER')")
    @CacheEvict(value = "paymentTypes", allEntries = true)
    public PaymentType update(Long id, PaymentTypeRequest request) {
        PaymentType existing = paymentTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentType not found"));

        existing.setCode(request.getCode());
        existing.setLabel(request.getLabel());

        return paymentTypeRepository.save(existing);
    }

    @PreAuthorize("hasRole('ROLE_SUPER_USER')")
    @CacheEvict(value = "paymentTypes", allEntries = true)
    public void delete(Long id) {
        paymentTypeRepository.deleteById(id);
    }

}
