package com.appi147.expensetracker.repository;

import com.appi147.expensetracker.entity.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {

    @Query("SELECT s FROM SubCategory s JOIN FETCH s.createdBy JOIN FETCH s.category WHERE s.subCategoryId = :id")
    Optional<SubCategory> findByIdWithCreator(Long id);

    @Query("""
                SELECT s FROM SubCategory s
                JOIN FETCH s.createdBy
                JOIN FETCH s.category
                WHERE s.category.categoryId = :categoryId AND s.createdBy.id = :userId
                ORDER BY s.updatedAt
            """)
    List<SubCategory> findAllByCategoryIdAndUserId(
            @Param("categoryId") Long categoryId,
            @Param("userId") String userId
    );

    @Query("SELECT DISTINCT s.category.categoryId FROM SubCategory s WHERE s.createdBy.userId = :userId")
    Set<Long> findUsedCategoryIdsByUser(@Param("userId") String userId);

}