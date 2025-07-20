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
        log.info("[SubCategoryService] Fetching all sub-categories for categoryId={} by userId={}", categoryId, requester.getUserId());

        Set<Long> subCategoryIdsUsedInExpenses = expenseRepository.findDistinctSubCategoryIdsByUserId(requester.getUserId());
        List<SubCategory> subCategories = subCategoryRepository.findAllByCategoryIdAndUserId(categoryId, requester.getUserId());
        log.debug("[SubCategoryService] Fetched {} sub-categories for categoryId={} and userId={}", subCategories.size(), categoryId, requester.getUserId());

        for (SubCategory subCategory : subCategories) {
            boolean usedInExpenses = subCategoryIdsUsedInExpenses.contains(subCategory.getSubCategoryId());
            subCategory.setDeletable(!usedInExpenses);
            log.debug("[SubCategoryService] SubCategory id={} (label={}) deletable={}", subCategory.getSubCategoryId(), subCategory.getLabel(), subCategory.isDeletable());
        }
        return subCategories;
    }

    public SubCategory createSubCategory(SubCategoryCreateRequest request) {
        User requester = UserContext.getCurrentUser();
        log.info("[SubCategoryService] User [{}] creating sub-category label='{}' under categoryId={}", requester.getUserId(), request.getLabel(), request.getCategoryId());

        Category category = categoryRepository.findByIdWithCreator(request.getCategoryId())
                .orElseThrow(() -> {
                    log.warn("[SubCategoryService] Category not found: categoryId={} for userId={}", request.getCategoryId(), requester.getUserId());
                    return new ResourceNotFoundException("Category not found");
                });

        if (!category.getCreatedBy().getUserId().equals(requester.getUserId())) {
            log.warn("[SubCategoryService] User [{}] attempted to create sub-category in category [{}] not owned by them", requester.getUserId(), request.getCategoryId());
            throw new ForbiddenException("You don't own the parent category");
        }

        SubCategory subCategory = new SubCategory();
        subCategory.setLabel(request.getLabel());
        subCategory.setCategory(category);
        subCategory.setCreatedBy(requester);

        SubCategory created = subCategoryRepository.saveAndFlush(subCategory);
        log.info("[SubCategoryService] SubCategory created: id={}, label='{}', userId={}", created.getSubCategoryId(), created.getLabel(), requester.getUserId());
        return created;
    }

    public SubCategory getSubCategory(Long subCategoryId) {
        log.info("[SubCategoryService] Getting sub-category id={}", subCategoryId);
        return getSubCategoryIfOwnedByCurrentUser(subCategoryId);
    }

    public SubCategory editSubCategory(Long subCategoryId, LabelUpdateRequest request) {
        log.info("[SubCategoryService] Editing sub-category id={}, new label='{}'", subCategoryId, request.getLabel());
        SubCategory subCategory = getSubCategoryIfOwnedByCurrentUser(subCategoryId);
        String oldLabel = subCategory.getLabel();
        subCategory.setLabel(request.getLabel());
        SubCategory updated = subCategoryRepository.saveAndFlush(subCategory);
        log.info("[SubCategoryService] SubCategory id={} label changed from '{}' to '{}' by user [{}]", subCategoryId, oldLabel, updated.getLabel(), updated.getCreatedBy().getUserId());
        return updated;
    }

    public void deleteSubCategory(Long subCategoryId) {
        User requester = UserContext.getCurrentUser();
        log.info("[SubCategoryService] User [{}] deleting sub-category id={}", requester.getUserId(), subCategoryId);
        SubCategory subCategory = getSubCategoryIfOwnedByCurrentUser(subCategoryId);
        subCategoryRepository.delete(subCategory);
        log.info("[SubCategoryService] SubCategory id={} deleted by user [{}]", subCategoryId, requester.getUserId());
    }

    private SubCategory getSubCategoryIfOwnedByCurrentUser(Long subCategoryId) {
        User requester = UserContext.getCurrentUser();
        SubCategory subCategory = subCategoryRepository.findByIdWithCreator(subCategoryId)
                .orElseThrow(() -> {
                    log.warn("[SubCategoryService] SubCategory id={} not found for user [{}]", subCategoryId, requester.getUserId());
                    return new ResourceNotFoundException("SubCategory not found with id " + subCategoryId);
                });

        if (!subCategory.getCreatedBy().getUserId().equals(requester.getUserId())) {
            log.warn("[SubCategoryService] Access denied: user [{}] tried to access sub-category [{}] not owned by them", requester.getUserId(), subCategoryId);
            throw new ForbiddenException("Access denied");
        }
        log.debug("[SubCategoryService] SubCategory id={} accessed by owner user [{}]", subCategoryId, requester.getUserId());
        return subCategory;
    }
}
