package com.appi147.expensetracker.repository;

import com.appi147.expensetracker.entity.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
}