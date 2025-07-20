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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    SubCategoryRepository subCategoryRepository;

    @Mock
    ExpenseRepository expenseRepository;

    @InjectMocks
    CategoryService categoryService;

    // --- getAllCategoriesForCurrentUser ---

    @Test
    void getAllCategoriesForCurrentUser_populatesDeletable_correctly() {
        User user = new User();
        user.setUserId("u1");
        Category cat1 = new Category();
        cat1.setCategoryId(1L);
        Category cat2 = new Category();
        cat2.setCategoryId(2L);
        Category cat3 = new Category();
        cat3.setCategoryId(3L);
        List<Category> cats = Arrays.asList(cat1, cat2, cat3);
        Set<Long> usedBySubCat = Set.of(1L);       // category 1 used in subcat
        Set<Long> usedByExpenses = Set.of(3L);     // category 3 used in expense

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);

            when(categoryRepository.findAllByCreatedBy_UserId("u1")).thenReturn(cats);
            when(subCategoryRepository.findUsedCategoryIdsByUser("u1")).thenReturn(usedBySubCat);
            when(expenseRepository.findDistinctCategoryIdsByUserId("u1")).thenReturn(usedByExpenses);

            List<Category> result = categoryService.getAllCategoriesForCurrentUser();

            assertSame(cats, result);
            assertFalse(cat1.isDeletable()); // Used in subcat
            assertTrue(cat2.isDeletable());  // Not used anywhere
            assertFalse(cat3.isDeletable()); // Used in expense
        }
    }

    @Test
    void getAllCategoriesForCurrentUser_nullUser_throwsNPE() {
        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(null);
            assertThrows(NullPointerException.class, () ->
                    categoryService.getAllCategoriesForCurrentUser());
        }
    }

    // --- getCategory ---

    @Test
    void getCategory_returnsCategoryIfOwned() {
        Category cat = new Category();
        cat.setCategoryId(1L);
        User u = new User();
        u.setUserId("123");
        cat.setCreatedBy(u);

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(u);
            when(categoryRepository.findByIdWithCreator(1L)).thenReturn(Optional.of(cat));
            Category ret = categoryService.getCategory(1L);

            assertSame(cat, ret);
            assertEquals(u, ret.getCreatedBy());
        }
    }

    @Test
    void getCategory_notFound_throwsResourceNotFound() {
        User u = new User();
        u.setUserId("x");
        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(u);
            when(categoryRepository.findByIdWithCreator(1L)).thenReturn(Optional.empty());

            Exception ex = assertThrows(ResourceNotFoundException.class,
                    () -> categoryService.getCategory(1L));
            assertEquals("Category not found with id 1", ex.getMessage());
        }
    }

    @Test
    void getCategory_wrongOwner_throwsForbidden() {
        User current = new User();
        current.setUserId("A");
        User owner = new User();
        owner.setUserId("B");
        Category cat = new Category();
        cat.setCreatedBy(owner);
        cat.setCategoryId(2L);

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(current);
            when(categoryRepository.findByIdWithCreator(2L)).thenReturn(Optional.of(cat));

            ForbiddenException fe = assertThrows(ForbiddenException.class,
                    () -> categoryService.getCategory(2L));
            assertTrue(fe.getMessage().contains("not allowed"));
        }
    }

    // --- createCategory ---

    @Test
    void createCategory_success_setsAllFields() {
        CategoryCreateRequest req = new CategoryCreateRequest();
        req.setLabel("TestLabel");
        User user = new User();
        user.setUserId("UUU");
        Category expected = new Category();
        expected.setCategoryId(9L);
        expected.setLabel("TestLabel");
        expected.setCreatedBy(user);

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);

            when(categoryRepository.saveAndFlush(any(Category.class))).thenReturn(expected);

            Category result = categoryService.createCategory(req);

            assertEquals(9L, result.getCategoryId());
            assertEquals("TestLabel", result.getLabel());
            assertSame(user, result.getCreatedBy());
        }
    }

    @Test
    void createCategory_nullUser_throwsNPE() {
        CategoryCreateRequest req = new CategoryCreateRequest();
        req.setLabel("Doesn't Matter");
        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(null);
            assertThrows(NullPointerException.class, () -> categoryService.createCategory(req));
        }
    }

    // --- editCategory ---

    @Test
    void editCategory_success_changesLabel() {
        User user = new User();
        user.setUserId("abc");
        Category cat = new Category();
        cat.setCategoryId(2L);
        cat.setCreatedBy(user);
        cat.setLabel("old");
        LabelUpdateRequest req = new LabelUpdateRequest();
        req.setLabel("newlabel");

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);
            when(categoryRepository.findByIdWithCreator(2L)).thenReturn(Optional.of(cat));
            when(categoryRepository.saveAndFlush(cat)).thenReturn(cat);

            Category res = categoryService.editCategory(2L, req);

            assertEquals(cat, res);
            assertEquals("newlabel", cat.getLabel());
        }
    }

    @Test
    void editCategory_notOwner_throwsForbidden() {
        User requester = new User();
        requester.setUserId("a");
        User owner = new User();
        owner.setUserId("b");
        Category cat = new Category();
        cat.setCreatedBy(owner);
        cat.setCategoryId(2L);
        LabelUpdateRequest req = new LabelUpdateRequest();
        req.setLabel("irrelevant");

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(requester);
            when(categoryRepository.findByIdWithCreator(2L)).thenReturn(Optional.of(cat));
            assertThrows(ForbiddenException.class, () -> categoryService.editCategory(2L, req));
        }
    }

    // --- deleteCategory ---

    @Test
    void deleteCategory_deletesIfNotUsed() {
        User user = new User();
        user.setUserId("A");
        Category cat = new Category();
        cat.setCategoryId(22L);
        cat.setCreatedBy(user);

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);
            when(categoryRepository.findByIdWithCreator(22L)).thenReturn(Optional.of(cat));
            when(subCategoryRepository.findUsedCategoryIdsByUser("A")).thenReturn(Collections.emptySet());

            categoryService.deleteCategory(22L);
            verify(categoryRepository).delete(cat);
        }
    }

    @Test
    void deleteCategory_usedInSubcategory_throwsForbidden() {
        User user = new User();
        user.setUserId("userX");
        Category cat = new Category();
        cat.setCategoryId(5L);
        cat.setCreatedBy(user);

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);
            when(categoryRepository.findByIdWithCreator(5L)).thenReturn(Optional.of(cat));
            when(subCategoryRepository.findUsedCategoryIdsByUser("userX")).thenReturn(Set.of(5L));

            ForbiddenException fe = assertThrows(ForbiddenException.class,
                    () -> categoryService.deleteCategory(5L));
            assertTrue(fe.getMessage().contains("Cannot delete category"));
            verify(categoryRepository, never()).delete(any());
        }
    }

    @Test
    void deleteCategory_notOwner_throwsForbidden() {
        User current = new User();
        current.setUserId("now");
        User owner = new User();
        owner.setUserId("other");
        Category cat = new Category();
        cat.setCategoryId(41L);
        cat.setCreatedBy(owner);

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(current);
            when(categoryRepository.findByIdWithCreator(41L)).thenReturn(Optional.of(cat));
            assertThrows(ForbiddenException.class, () -> categoryService.deleteCategory(41L));
            verify(categoryRepository, never()).delete(any());
        }
    }

    // --- getCategoryIfOwnedByCurrentUser (private, so exercised transitively above) ---
}

