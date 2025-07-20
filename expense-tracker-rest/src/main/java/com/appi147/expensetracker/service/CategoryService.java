package com.appi147.expensetracker.service;

import com.appi147.expensetracker.auth.UserContext;
import com.appi147.expensetracker.entity.Category;
import com.appi147.expensetracker.entity.User;
import com.appi147.expensetracker.exception.ForbiddenException;
import com.appi147.expensetracker.exception.ResourceNotFoundException;
import com.appi147.expensetracker.model.request.CategoryCreateRequest;
import com.appi147.expensetracker.model.request.LabelUpdateRequest;
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
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final ExpenseRepository expenseRepository;

    public List<Category> getAllCategoriesForCurrentUser() {
        User requester = UserContext.getCurrentUser();
        log.info("[CategoryService] Fetching all categories for userId={}", requester.getUserId());
        List<Category> categories = categoryRepository.findAllByCreatedBy_UserId(requester.getUserId());
        Set<Long> subCatUsed = subCategoryRepository.findUsedCategoryIdsByUser(requester.getUserId());
        Set<Long> expenseUsed = expenseRepository.findDistinctCategoryIdsByUserId(requester.getUserId());
        for (Category category : categories) {
            boolean usedInSubCategories = subCatUsed.contains(category.getCategoryId());
            boolean usedInExpenses = expenseUsed.contains(category.getCategoryId());
            category.setDeletable(!usedInSubCategories && !usedInExpenses);
            log.debug("[CategoryService] Category id={} (label={}) deletable={}",
                    category.getCategoryId(), category.getLabel(), category.isDeletable());
        }
        log.info("[CategoryService] Found {} categories for userId={}", categories.size(), requester.getUserId());
        return categories;
    }

    public Category getCategory(Long categoryId) {
        log.info("[CategoryService] Getting category id={}", categoryId);
        return getCategoryIfOwnedByCurrentUser(categoryId);
    }

    public Category createCategory(CategoryCreateRequest request) {
        User requester = UserContext.getCurrentUser();
        log.info("[CategoryService] User id={} creating category: label='{}'", requester.getUserId(), request.getLabel());
        Category category = new Category();
        category.setLabel(request.getLabel());
        category.setCreatedBy(requester);
        category = categoryRepository.saveAndFlush(category);
        log.info("[CategoryService] Category created: id={}, label='{}', userId={}", category.getCategoryId(), category.getLabel(), requester.getUserId());
        return category;
    }

    public Category editCategory(Long categoryId, LabelUpdateRequest request) {
        log.info("[CategoryService] Editing category id={} with new label='{}'", categoryId, request.getLabel());
        Category category = getCategoryIfOwnedByCurrentUser(categoryId);
        String oldLabel = category.getLabel();
        category.setLabel(request.getLabel());
        category = categoryRepository.saveAndFlush(category);
        log.info("[CategoryService] Category id={} label changed from '{}' to '{}' by userId={}", categoryId, oldLabel, category.getLabel(), category.getCreatedBy().getUserId());
        return category;
    }

    public void deleteCategory(Long categoryId) {
        User requester = UserContext.getCurrentUser();
        log.info("[CategoryService] User id={} attempting to delete category id={}", requester.getUserId(), categoryId);
        Category category = getCategoryIfOwnedByCurrentUser(categoryId);
        boolean isUsedInSubCat = subCategoryRepository
                .findUsedCategoryIdsByUser(requester.getUserId())
                .contains(categoryId);
        if (isUsedInSubCat) {
            log.warn("[CategoryService] Delete denied: category id={} is used in subcategories for user id={}", categoryId, requester.getUserId());
            throw new ForbiddenException("Cannot delete category: it is used in one or more subcategories.");
        }
        log.info("[CategoryService] Deleting category id={} by user id={}", categoryId, requester.getUserId());
        categoryRepository.delete(category);
        log.info("[CategoryService] Category id={} deleted by user id={}", categoryId, requester.getUserId());
    }

    private Category getCategoryIfOwnedByCurrentUser(Long categoryId) {
        User requester = UserContext.getCurrentUser();
        Category category = categoryRepository.findByIdWithCreator(categoryId)
                .orElseThrow(() -> {
                    log.warn("[CategoryService] Category id={} not found for user id={}", categoryId, requester.getUserId());
                    return new ResourceNotFoundException("Category not found with id " + categoryId);
                });

        if (category.getCreatedBy() == null || !category.getCreatedBy().getUserId().equals(requester.getUserId())) {
            log.warn("[CategoryService] Access denied: userId={} tried to access category id={} not owned by them. Owned by userId={}",
                    requester.getUserId(), categoryId,
                    category.getCreatedBy() != null ? category.getCreatedBy().getUserId() : "null"
            );
            throw new ForbiddenException("You are not allowed to access this resource");
        }
        log.debug("[CategoryService] Category id={} accessed by owner user id={}", categoryId, requester.getUserId());
        return category;
    }
}
