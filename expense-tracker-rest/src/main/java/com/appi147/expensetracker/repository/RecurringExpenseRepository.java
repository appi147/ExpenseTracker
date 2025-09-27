package com.appi147.expensetracker.repository;

import com.appi147.expensetracker.entity.RecurringExpense;
import com.appi147.expensetracker.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecurringExpenseRepository extends JpaRepository<RecurringExpense, Long> {
    @EntityGraph(attributePaths = {
            "subCategory",
            "subCategory.category",
            "paymentType"
    })
    @Query("select r from RecurringExpense r where r.createdBy = ?1")
    List<RecurringExpense> findByCreatedBy(User createdBy);

    @Query("select r from RecurringExpense r where r.dayOfMonth = ?1")
    List<RecurringExpense> findByDayOfMonth(int dayOfMonth);

}