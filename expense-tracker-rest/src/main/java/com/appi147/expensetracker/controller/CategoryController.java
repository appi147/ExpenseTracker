package com.appi147.expensetracker.controller;

import com.appi147.expensetracker.entity.Category;
import com.appi147.expensetracker.model.request.CategoryCreateRequest;
import com.appi147.expensetracker.model.request.LabelUpdateRequest;
import com.appi147.expensetracker.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/category")
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/create")
    @Operation(summary = "Create a new category", responses = {
            @ApiResponse(responseCode = "200", description = "Category created"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public Category createCategory(@RequestBody CategoryCreateRequest categoryCreateRequest) {
        return categoryService.createCategory(categoryCreateRequest);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a category by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Category found"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public Category getCategory(@PathVariable Long id) {
        return categoryService.getCategory(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edit a category", responses = {
            @ApiResponse(responseCode = "200", description = "Category updated"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public Category editCategory(@PathVariable Long id, @RequestBody LabelUpdateRequest request) {
        return categoryService.editCategory(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category", responses = {
            @ApiResponse(responseCode = "200", description = "Category deleted"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }

    @GetMapping
    @Operation(summary = "Get all categories of current user", responses = {
            @ApiResponse(responseCode = "200", description = "List returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public List<Category> getAllCategoriesForCurrentUser() {
        return categoryService.getAllCategoriesForCurrentUser();
    }
}
