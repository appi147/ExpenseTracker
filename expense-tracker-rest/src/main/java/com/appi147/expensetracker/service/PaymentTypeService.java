package com.appi147.expensetracker.service;

import com.appi147.expensetracker.auth.UserContext;
import com.appi147.expensetracker.entity.PaymentType;
import com.appi147.expensetracker.exception.ResourceNotFoundException;
import com.appi147.expensetracker.model.request.PaymentTypeRequest;
import com.appi147.expensetracker.repository.PaymentTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentTypeService {

    private final PaymentTypeRepository paymentTypeRepository;

    @Cacheable(value = "paymentTypes", key = "#code")
    public PaymentType getByCode(String code) {
        log.info("[PaymentTypeService] Fetching PaymentType by code '{}'", code);
        return paymentTypeRepository.findByCode(code)
                .orElseThrow(() -> {
                    log.warn("[PaymentTypeService] PaymentType not found for code: '{}'", code);
                    return new ResourceNotFoundException("PaymentType not found for code: " + code);
                });
    }

    @Cacheable(value = "paymentTypes")
    public List<PaymentType> getAll() {
        log.info("[PaymentTypeService] Fetching all PaymentTypes");
        List<PaymentType> types = paymentTypeRepository.findAll();
        log.info("[PaymentTypeService] Found {} payment types", types.size());
        return types;
    }

    @PreAuthorize("hasRole('ROLE_SUPER_USER')")
    @CacheEvict(value = "paymentTypes", allEntries = true)
    public PaymentType create(PaymentTypeRequest request) {
        var requester = UserContext.getCurrentUser();
        log.info("[PaymentTypeService] User [{}] creating PaymentType: code='{}', label='{}'",
                requester.getUserId(), request.getCode(), request.getLabel());

        PaymentType paymentType = new PaymentType();
        paymentType.setCreatedBy(requester);
        paymentType.setCode(request.getCode());
        paymentType.setLabel(request.getLabel());

        PaymentType created = paymentTypeRepository.save(paymentType);

        log.info("[PaymentTypeService] PaymentType created: id={}, code='{}', userId={}",
                created.getId(), created.getCode(), requester.getUserId());
        return created;
    }

    @PreAuthorize("hasRole('ROLE_SUPER_USER')")
    @CacheEvict(value = "paymentTypes", allEntries = true)
    public PaymentType update(Long id, PaymentTypeRequest request) {
        log.info("[PaymentTypeService] Updating PaymentType id={}, new code='{}', new label='{}'",
                id, request.getCode(), request.getLabel());

        PaymentType existing = paymentTypeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[PaymentTypeService] PaymentType not found for update: id={}", id);
                    return new ResourceNotFoundException("PaymentType not found");
                });

        existing.setCode(request.getCode());
        existing.setLabel(request.getLabel());
        PaymentType updated = paymentTypeRepository.save(existing);

        log.info("[PaymentTypeService] PaymentType updated: id={}, code='{}'", updated.getId(), updated.getCode());
        return updated;
    }

    @PreAuthorize("hasRole('ROLE_SUPER_USER')")
    @CacheEvict(value = "paymentTypes", allEntries = true)
    public void delete(Long id) {
        log.info("[PaymentTypeService] Deleting PaymentType id={}", id);
        paymentTypeRepository.deleteById(id);
        log.info("[PaymentTypeService] PaymentType deleted: id={}", id);
    }
}
