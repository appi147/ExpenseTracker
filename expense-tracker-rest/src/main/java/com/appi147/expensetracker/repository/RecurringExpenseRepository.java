package com.appi147.expensetracker.repository;

import com.appi147.expensetracker.entity.RecurringExpense;
import com.appi147.expensetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecurringExpenseRepository extends JpaRepository<RecurringExpense, Long> {
    @Query("select r from RecurringExpense r where r.createdBy = ?1")
    List<RecurringExpense> findByCreatedBy(User createdBy);

}