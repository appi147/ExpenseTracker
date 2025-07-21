package com.appi147.expensetracker.controller;

import com.appi147.expensetracker.entity.Category;
import com.appi147.expensetracker.model.request.CategoryCreateRequest;
import com.appi147.expensetracker.model.request.LabelUpdateRequest;
import com.appi147.expensetracker.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Category", description = "Category-related operations")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/create")
    @Operation(summary = "Create a new category", responses = {
            @ApiResponse(responseCode = "201", description = "Category created"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Category> createCategory(@Valid @RequestBody CategoryCreateRequest categoryCreateRequest) {
        Category category = categoryService.createCategory(categoryCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a category by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Category found"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Category> getCategory(@PathVariable Long id) {
        Category category = categoryService.getCategory(id);
        return ResponseEntity.ok(category);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edit a category", responses = {
            @ApiResponse(responseCode = "200", description = "Category updated"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Category> editCategory(@PathVariable Long id, @Valid @RequestBody LabelUpdateRequest request) {
        Category updated = categoryService.editCategory(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category", responses = {
            @ApiResponse(responseCode = "204", description = "Category deleted"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Get all categories of current user", responses = {
            @ApiResponse(responseCode = "200", description = "List returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<Category>> getAllCategoriesForCurrentUser() {
        List<Category> categories = categoryService.getAllCategoriesForCurrentUser();
        return ResponseEntity.ok(categories);
    }
}
