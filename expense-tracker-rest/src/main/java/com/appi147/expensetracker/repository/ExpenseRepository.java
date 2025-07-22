package com.appi147.expensetracker.repository;

import com.appi147.expensetracker.entity.Expense;
import com.appi147.expensetracker.entity.User;
import com.appi147.expensetracker.projection.MonthlyCategoryWiseExpense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ExpenseRepository extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense> {

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

    @EntityGraph(attributePaths = {
            "subCategory",
            "subCategory.category"
    })
    @Query("select e from Expense e where e.createdBy = ?1 and e.date between ?2 and ?3")
    List<Expense> findByCreatedByAndDateBetween(User createdBy, LocalDate dateStart, LocalDate dateEnd);


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

    @EntityGraph(attributePaths = {
            "subCategory",
            "subCategory.category",
            "paymentType"
    })
    Page<Expense> findAll(Specification<Expense> spec, Pageable pageable);

    @Query("SELECT e FROM Expense e JOIN FETCH e.createdBy WHERE e.expenseId = :id")
    Optional<Expense> findByIdWithCreator(@Param("id") Long id);

    @Query(value = """
            SELECT
                TO_CHAR(e.date, 'YYYY-MM') AS month,
                c.label AS category,
                SUM(e.amount) AS totalAmount
            FROM {h-schema}expense e
            JOIN {h-schema}sub_category sc ON e.sub_category_id = sc.sub_cat_id
            JOIN {h-schema}category c ON sc.cat_id = c.cat_id
            WHERE e.user_id = :userId
              AND e.date >= date_trunc('month', CURRENT_DATE) - INTERVAL '11 months'
              AND e.date < date_trunc('month', CURRENT_DATE) + INTERVAL '1 month'
            GROUP BY month, category
            ORDER BY month ASC, category ASC
            """, nativeQuery = true)
    List<MonthlyCategoryWiseExpense> getMonthlyCategoryTrends(@Param("userId") String userId);
}
