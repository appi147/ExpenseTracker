package com.appi147.expensetracker.service;

import com.appi147.expensetracker.auth.UserContext;
import com.appi147.expensetracker.entity.Category;
import com.appi147.expensetracker.entity.SubCategory;
import com.appi147.expensetracker.entity.User;
import com.appi147.expensetracker.exception.ForbiddenException;
import com.appi147.expensetracker.exception.ResourceNotFoundException;
import com.appi147.expensetracker.model.request.LabelUpdateRequest;
import com.appi147.expensetracker.model.request.SubCategoryCreateRequest;
import com.appi147.expensetracker.repository.CategoryRepository;
import com.appi147.expensetracker.repository.ExpenseRepository;
import com.appi147.expensetracker.repository.SubCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;

    public List<SubCategory> getAllSubCategories(Long categoryId) {
        User requester = UserContext.getCurrentUser();
        Set<Long> subCategoryIdsUsedInExpenses = expenseRepository.findDistinctSubCategoryIdsByUserId(requester.getUserId());
        List<SubCategory> subCategories = subCategoryRepository.findAllByCategoryIdAndUserId(categoryId, requester.getUserId());
        for (SubCategory subCategory : subCategories) {
            boolean usedInExpenses = subCategoryIdsUsedInExpenses.contains(subCategory.getSubCategoryId());
            subCategory.setDeletable(!usedInExpenses);
        }
        return subCategories;
    }

    public SubCategory createSubCategory(SubCategoryCreateRequest request) {
        User requester = UserContext.getCurrentUser();

        Category category = categoryRepository.findByIdWithCreator(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (!category.getCreatedBy().getUserId().equals(requester.getUserId())) {
            throw new ForbiddenException("You don't own the parent category");
        }

        SubCategory subCategory = new SubCategory();
        subCategory.setLabel(request.getLabel());
        subCategory.setCategory(category);
        subCategory.setCreatedBy(requester);
        return subCategoryRepository.saveAndFlush(subCategory);
    }

    public SubCategory getSubCategory(Long subCategoryId) {
        return getSubCategoryIfOwnedByCurrentUser(subCategoryId);
    }

    public SubCategory editSubCategory(Long subCategoryId, LabelUpdateRequest request) {
        SubCategory subCategory = getSubCategoryIfOwnedByCurrentUser(subCategoryId);
        subCategory.setLabel(request.getLabel());
        return subCategoryRepository.saveAndFlush(subCategory);
    }

    public void deleteSubCategory(Long subCategoryId) {
        SubCategory subCategory = getSubCategoryIfOwnedByCurrentUser(subCategoryId);
        subCategoryRepository.delete(subCategory);
    }

    private SubCategory getSubCategoryIfOwnedByCurrentUser(Long subCategoryId) {
        User requester = UserContext.getCurrentUser();
        SubCategory subCategory = subCategoryRepository.findByIdWithCreator(subCategoryId)
                .orElseThrow(() -> new ResourceNotFoundException("SubCategory not found with id " + subCategoryId));

        if (!subCategory.getCreatedBy().getUserId().equals(requester.getUserId())) {
            log.warn("User [{}] tried to access sub-category [{}] not owned by them", requester.getUserId(), subCategoryId);
            throw new ForbiddenException("Access denied");
        }
        return subCategory;
    }
}
