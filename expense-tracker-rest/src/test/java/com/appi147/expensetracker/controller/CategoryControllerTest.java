package com.appi147.expensetracker.controller;

import com.appi147.expensetracker.entity.Category;
import com.appi147.expensetracker.exception.GlobalExceptionHandler;
import com.appi147.expensetracker.model.request.CategoryCreateRequest;
import com.appi147.expensetracker.model.request.LabelUpdateRequest;
import com.appi147.expensetracker.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CategoryController.class)
@Import({CategoryControllerTest.TestConfig.class, GlobalExceptionHandler.class})
@WithMockUser
class CategoryControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CategoryService categoryService() {
            return Mockito.mock(CategoryService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryService categoryService;

    @Test
    void createCategory_shouldReturnCreated() throws Exception {
        CategoryCreateRequest request = new CategoryCreateRequest("Food");
        Category category = new Category();
        category.setLabel("Food");

        when(categoryService.createCategory(any())).thenReturn(category);

        mockMvc.perform(post("/category/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "label": "Food" }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.label").value("Food"));
    }

    @Test
    void getCategory_shouldReturnCategory() throws Exception {
        Category cat = new Category();
        cat.setCategoryId(1L);
        cat.setLabel("Travel");

        when(categoryService.getCategory(1L)).thenReturn(cat);

        mockMvc.perform(get("/category/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value("Travel"));
    }

    @Test
    void editCategory_shouldReturnUpdated() throws Exception {
        LabelUpdateRequest updateRequest = new LabelUpdateRequest("New Label");
        Category updated = new Category();
        updated.setLabel("New Label");

        when(categoryService.editCategory(eq(1L), any())).thenReturn(updated);

        mockMvc.perform(put("/category/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "label": "New Label" }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value("New Label"));
    }

    @Test
    void deleteCategory_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/category/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(categoryService).deleteCategory(1L);
    }

    @Test
    void getAllCategories_shouldReturnList() throws Exception {
        when(categoryService.getAllCategoriesForCurrentUser()).thenReturn(List.of(new Category()));

        mockMvc.perform(get("/category").with(csrf()))
                .andExpect(status().isOk());
    }
}
