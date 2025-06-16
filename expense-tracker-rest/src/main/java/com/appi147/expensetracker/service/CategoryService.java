package com.appi147.expensetracker.service;

import com.appi147.expensetracker.auth.UserContext;
import com.appi147.expensetracker.entity.Category;
import com.appi147.expensetracker.entity.User;
import com.appi147.expensetracker.exception.ForbiddenException;
import com.appi147.expensetracker.exception.ResourceNotFoundException;
import com.appi147.expensetracker.model.request.CategoryCreateRequest;
import com.appi147.expensetracker.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategoriesForCurrentUser() {
        User requester = UserContext.getCurrentUser();
        return categoryRepository.findAllByCreatedBy_UserId(requester.getUserId());
    }

    public Category getCategory(Long categoryId) {
        return getCategoryIfOwnedByCurrentUser(categoryId);
    }

    public Category createCategory(CategoryCreateRequest request) {
        User requester = UserContext.getCurrentUser();
        log.info("Category creation request by [{}]", requester.getUserId());
        Category category = new Category();
        category.setLabel(request.getLabel());
        category.setCreatedBy(requester);
        return categoryRepository.saveAndFlush(category);
    }

    public Category editCategory(Long categoryId, CategoryCreateRequest request) {
        Category category = getCategoryIfOwnedByCurrentUser(categoryId);
        log.info("Category edit by user [{}] for category [{}]", category.getCreatedBy().getUserId(), categoryId);
        category.setLabel(request.getLabel());
        return categoryRepository.saveAndFlush(category);
    }

    public void deleteCategory(Long categoryId) {
        Category category = getCategoryIfOwnedByCurrentUser(categoryId);
        log.info("Deleting category [{}] by user [{}]", categoryId, category.getCreatedBy().getUserId());
        categoryRepository.delete(category);
    }

    private Category getCategoryIfOwnedByCurrentUser(Long categoryId) {
        User requester = UserContext.getCurrentUser();
        Category category = categoryRepository.findByIdWithCreator(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + categoryId));

        if (category.getCreatedBy() == null || !category.getCreatedBy().getUserId().equals(requester.getUserId())) {
            log.warn("User [{}] tried to access category [{}] not owned by them", requester.getUserId(), categoryId);
            throw new ForbiddenException("You are not allowed to access this resource");
        }

        return category;
    }
}
