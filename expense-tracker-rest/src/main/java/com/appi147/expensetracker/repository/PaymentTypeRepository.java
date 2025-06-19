package com.appi147.expensetracker.repository;

import com.appi147.expensetracker.entity.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentTypeRepository extends JpaRepository<PaymentType, Long> {

    Optional<PaymentType> findByCode(String code);
}