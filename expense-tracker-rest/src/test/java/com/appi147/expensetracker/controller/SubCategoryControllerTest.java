package com.appi147.expensetracker.controller;

import com.appi147.expensetracker.entity.SubCategory;
import com.appi147.expensetracker.exception.GlobalExceptionHandler;
import com.appi147.expensetracker.service.SubCategoryService;
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

@WebMvcTest(controllers = SubCategoryController.class)
@Import({GlobalExceptionHandler.class, SubCategoryControllerTest.TestConfig.class})
@WithMockUser
class SubCategoryControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public SubCategoryService subCategoryService() {
            return Mockito.mock(SubCategoryService.class);
        }
    }

    @Autowired
    private SubCategoryService subCategoryService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void create_shouldReturnCreated() throws Exception {
        SubCategory mock = new SubCategory();
        mock.setLabel("Dining");

        when(subCategoryService.createSubCategory(any())).thenReturn(mock);

        mockMvc.perform(post("/sub-category/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            { "label": "Dining", "categoryId": 1 }
                        """))
                .andExpect(status().isCreated());
    }

    @Test
    void getById_shouldReturnSubCategory() throws Exception {
        SubCategory sc = new SubCategory();
        sc.setSubCategoryId(1L);
        sc.setLabel("Dining");

        when(subCategoryService.getSubCategory(1L)).thenReturn(sc);

        mockMvc.perform(get("/sub-category/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value("Dining"));
    }

    @Test
    void getAll_shouldReturnList() throws Exception {
        when(subCategoryService.getAllSubCategories(1L)).thenReturn(List.of(new SubCategory()));

        mockMvc.perform(get("/sub-category?categoryId=1"))
                .andExpect(status().isOk());
    }

    @Test
    void update_shouldReturnUpdated() throws Exception {
        SubCategory updated = new SubCategory();
        updated.setLabel("Updated Label");

        when(subCategoryService.editSubCategory(eq(1L), any())).thenReturn(updated);

        mockMvc.perform(put("/sub-category/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            { "label": "Updated Label" }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value("Updated Label"));
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/sub-category/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(subCategoryService).deleteSubCategory(1L);
    }
}

