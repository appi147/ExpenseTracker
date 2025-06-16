package com.appi147.expensetracker.repository;

import com.appi147.expensetracker.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c JOIN FETCH c.createdBy WHERE c.categoryId = :id")
    Optional<Category> findByIdWithCreator(@Param("id") Long id);

    List<Category> findAllByCreatedBy_UserId(String userId);
}
