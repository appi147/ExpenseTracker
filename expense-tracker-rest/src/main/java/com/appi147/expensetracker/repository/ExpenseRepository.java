package com.appi147.expensetracker.repository;

import com.appi147.expensetracker.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("""
                SELECT COALESCE(SUM(e.amount), 0)
                FROM Expense e
                WHERE e.createdBy.id = :userId
                  AND e.date BETWEEN :startDate AND :endDate
            """)
    BigDecimal getSumOfExpensesBetweenDatesForUser(
            @Param("userId") String userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
                SELECT DISTINCT e.subCategory.category.categoryId
                FROM Expense e
                WHERE e.createdBy.id = :userId
            """)
    Set<Long> findDistinctCategoryIdsByUserId(@Param("userId") String userId);

    @Query("""
                SELECT DISTINCT e.subCategory.subCategoryId
                FROM Expense e
                WHERE e.createdBy.id = :userId
            """)
    Set<Long> findDistinctSubCategoryIdsByUserId(@Param("userId") String userId);
}
