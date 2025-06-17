package com.appi147.expensetracker.service;

import com.appi147.expensetracker.auth.UserContext;
import com.appi147.expensetracker.entity.Category;
import com.appi147.expensetracker.entity.SubCategory;
import com.appi147.expensetracker.entity.User;
import com.appi147.expensetracker.exception.ForbiddenException;
import com.appi147.expensetracker.exception.ResourceNotFoundException;
import com.appi147.expensetracker.model.request.SubCategoryCreateRequest;
import com.appi147.expensetracker.repository.CategoryRepository;
import com.appi147.expensetracker.repository.SubCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;

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

    public List<SubCategory> getAllSubCategories() {
        User requester = UserContext.getCurrentUser();
        return subCategoryRepository.findAllByCreatedBy_UserId(requester.getUserId());
    }

    // We do not allow changing the parent category once a subcategory is created
    public SubCategory editSubCategory(Long subCategoryId, SubCategoryCreateRequest request) {
        SubCategory subCategory = getSubCategoryIfOwnedByCurrentUser(subCategoryId);

        if (!subCategory.getCategory().getCategoryId().equals(request.getCategoryId())) {
            throw new ForbiddenException("You cannot change the parent category of a sub-category.");
        }

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
