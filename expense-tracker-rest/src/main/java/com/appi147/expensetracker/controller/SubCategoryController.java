package com.appi147.expensetracker.controller;

import com.appi147.expensetracker.entity.SubCategory;
import com.appi147.expensetracker.model.request.LabelUpdateRequest;
import com.appi147.expensetracker.model.request.SubCategoryCreateRequest;
import com.appi147.expensetracker.service.SubCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sub-category")
@SecurityRequirement(name = "bearerAuth")
public class SubCategoryController {
    private final SubCategoryService subCategoryService;

    @PostMapping("/create")
    @Operation(summary = "Create a new sub-category", responses = {
            @ApiResponse(responseCode = "201", description = "Sub-category created"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - you don't own the category")
    })
    public ResponseEntity<SubCategory> create(@RequestBody SubCategoryCreateRequest request) {
        SubCategory subCategory = subCategoryService.createSubCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(subCategory);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get sub-category by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Sub-category found"),
            @ApiResponse(responseCode = "404", description = "Sub-category not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - you don't own this sub-category")
    })
    public ResponseEntity<SubCategory> get(@PathVariable Long id) {
        SubCategory subCategory = subCategoryService.getSubCategory(id);
        return ResponseEntity.ok(subCategory);
    }

    @GetMapping
    @Operation(summary = "List all sub-categories of current user and given category", responses = {
            @ApiResponse(responseCode = "200", description = "List returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<SubCategory>> getAll(@RequestParam Long categoryId) {
        List<SubCategory> subCategories = subCategoryService.getAllSubCategories(categoryId);
        return ResponseEntity.ok(subCategories);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a sub-category", responses = {
            @ApiResponse(responseCode = "200", description = "Sub-category updated"),
            @ApiResponse(responseCode = "404", description = "Sub-category not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - you don't own this sub-category")
    })
    public ResponseEntity<SubCategory> update(@PathVariable Long id, @RequestBody LabelUpdateRequest request) {
        SubCategory updated = subCategoryService.editSubCategory(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a sub-category", responses = {
            @ApiResponse(responseCode = "204", description = "Sub-category deleted"),
            @ApiResponse(responseCode = "404", description = "Sub-category not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - you don't own this sub-category")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        subCategoryService.deleteSubCategory(id);
        return ResponseEntity.noContent().build();
    }
}
