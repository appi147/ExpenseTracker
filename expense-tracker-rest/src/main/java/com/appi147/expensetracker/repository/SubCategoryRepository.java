package com.appi147.expensetracker.repository;

import com.appi147.expensetracker.entity.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {

    @Query("SELECT s FROM SubCategory s JOIN FETCH s.createdBy JOIN FETCH s.category WHERE s.subCategoryId = :id")
    Optional<SubCategory> findByIdWithCreator(Long id);

    List<SubCategory> findAllByCreatedBy_UserId(String userId);

}