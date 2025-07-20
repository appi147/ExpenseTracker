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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubCategoryServiceTest {

    @Mock
    SubCategoryRepository subCategoryRepository;

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    ExpenseRepository expenseRepository;

    @InjectMocks
    SubCategoryService subCategoryService;

    // ---- getAllSubCategories ----
    @Test
    void getAllSubCategories_marksDeletableProperly() {
        User user = new User();
        user.setUserId("jane");
        Long categoryId = 5L;

        SubCategory s1 = new SubCategory();
        s1.setSubCategoryId(1L);
        SubCategory s2 = new SubCategory();
        s2.setSubCategoryId(2L);
        SubCategory s3 = new SubCategory();
        s3.setSubCategoryId(3L);
        List<SubCategory> list = Arrays.asList(s1, s2, s3);
        Set<Long> usedInExpenses = Set.of(1L, 3L); // s1 and s3 are used

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);
            when(subCategoryRepository.findAllByCategoryIdAndUserId(categoryId, "jane"))
                    .thenReturn(list);
            when(expenseRepository.findDistinctSubCategoryIdsByUserId("jane"))
                    .thenReturn(usedInExpenses);

            List<SubCategory> result = subCategoryService.getAllSubCategories(categoryId);

            assertNotNull(result);
            assertEquals(list, result);

            // Used (not deletable)
            assertFalse(s1.isDeletable());
            assertTrue(s2.isDeletable());
            assertFalse(s3.isDeletable());
        }
    }

    @Test
    void getAllSubCategories_nullUser_throwsNPE() {
        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(null);
            assertThrows(NullPointerException.class, () -> subCategoryService.getAllSubCategories(2L));
        }
    }

    // ---- createSubCategory ----
    @Test
    void createSubCategory_success_returnsCreated() {
        User user = new User();
        user.setUserId("bob");
        SubCategoryCreateRequest req = mock(SubCategoryCreateRequest.class);
        when(req.getLabel()).thenReturn("Internet");
        when(req.getCategoryId()).thenReturn(101L);

        Category category = new Category();
        User categoryOwner = user; // Same as current user
        category.setCreatedBy(categoryOwner);

        SubCategory toReturn = new SubCategory();
        toReturn.setSubCategoryId(77L);
        toReturn.setLabel("Internet");
        toReturn.setCreatedBy(user);
        toReturn.setCategory(category);

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);

            when(categoryRepository.findByIdWithCreator(101L))
                    .thenReturn(Optional.of(category));
            when(subCategoryRepository.saveAndFlush(any(SubCategory.class)))
                    .thenReturn(toReturn);

            SubCategory result = subCategoryService.createSubCategory(req);

            assertNotNull(result);
            assertEquals("Internet", result.getLabel());
            assertEquals(user, result.getCreatedBy());
            assertEquals(category, result.getCategory());
            assertEquals(77L, result.getSubCategoryId());

            verify(req, atLeastOnce()).getLabel();
            verify(req, atLeastOnce()).getCategoryId();
            verify(subCategoryRepository).saveAndFlush(any(SubCategory.class));
        }
    }

    @Test
    void createSubCategory_categoryNotFound_throwsResourceNotFoundException() {
        User user = new User();
        user.setUserId("joe");
        SubCategoryCreateRequest req = mock(SubCategoryCreateRequest.class);
        when(req.getCategoryId()).thenReturn(111L);

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);
            when(categoryRepository.findByIdWithCreator(111L))
                    .thenReturn(Optional.empty());
            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                    () -> subCategoryService.createSubCategory(req));
            assertTrue(ex.getMessage().toLowerCase().contains("not found"));
        }
    }

    @Test
    void createSubCategory_wrongOwner_throwsForbidden() {
        User user = new User();
        user.setUserId("alice");
        SubCategoryCreateRequest req = mock(SubCategoryCreateRequest.class);
        when(req.getCategoryId()).thenReturn(222L);

        User other = new User();
        other.setUserId("bob");
        Category cat = new Category();
        cat.setCreatedBy(other);

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);
            when(categoryRepository.findByIdWithCreator(222L)).thenReturn(Optional.of(cat));

            ForbiddenException ex = assertThrows(ForbiddenException.class,
                    () -> subCategoryService.createSubCategory(req));
            assertTrue(ex.getMessage().toLowerCase().contains("own"));
        }
    }

    // ---- getSubCategory ----
    @Test
    void getSubCategory_success_ownedByUser() {
        User user = new User();
        user.setUserId("id11");
        SubCategory sc = new SubCategory();
        sc.setSubCategoryId(10L);
        sc.setCreatedBy(user);

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);
            when(subCategoryRepository.findByIdWithCreator(10L)).thenReturn(Optional.of(sc));

            SubCategory result = subCategoryService.getSubCategory(10L);
            assertSame(sc, result);
            assertEquals(user, result.getCreatedBy());
        }
    }

    @Test
    void getSubCategory_notFound_throwsResourceNotFound() {
        User user = new User();
        user.setUserId("abc");
        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);
            when(subCategoryRepository.findByIdWithCreator(99L)).thenReturn(Optional.empty());
            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                    () -> subCategoryService.getSubCategory(99L));
            assertTrue(ex.getMessage().toLowerCase().contains("not found"));
        }
    }

    @Test
    void getSubCategory_wrongOwner_throwsForbidden() {
        User user = new User();
        user.setUserId("me");
        User owner = new User();
        owner.setUserId("notme");
        SubCategory sc = new SubCategory();
        sc.setCreatedBy(owner);
        sc.setSubCategoryId(8L);

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);
            when(subCategoryRepository.findByIdWithCreator(8L)).thenReturn(Optional.of(sc));
            ForbiddenException ex = assertThrows(ForbiddenException.class,
                    () -> subCategoryService.getSubCategory(8L));
            assertTrue(ex.getMessage().toLowerCase().contains("access denied"));
        }
    }

    // ---- editSubCategory ----
    @Test
    void editSubCategory_success_updatesLabel() {
        User user = new User();
        user.setUserId("uuu");
        SubCategory sc = new SubCategory();
        sc.setSubCategoryId(2L);
        sc.setLabel("old");
        sc.setCreatedBy(user);
        LabelUpdateRequest req = mock(LabelUpdateRequest.class);
        when(req.getLabel()).thenReturn("newer");

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);

            when(subCategoryRepository.findByIdWithCreator(2L)).thenReturn(Optional.of(sc));
            when(subCategoryRepository.saveAndFlush(sc)).thenReturn(sc);

            SubCategory res = subCategoryService.editSubCategory(2L, req);

            assertNotNull(res);
            assertEquals("newer", res.getLabel());
            assertSame(sc, res);
        }
    }

    @Test
    void editSubCategory_wrongOwner_throwsForbidden() {
        User r = new User();
        r.setUserId("me");
        User owner = new User();
        owner.setUserId("notme");
        SubCategory sc = new SubCategory();
        sc.setCreatedBy(owner);
        LabelUpdateRequest req = mock(LabelUpdateRequest.class);

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(r);
            when(subCategoryRepository.findByIdWithCreator(anyLong())).thenReturn(Optional.of(sc));

            assertThrows(ForbiddenException.class,
                    () -> subCategoryService.editSubCategory(2L, req));
        }
    }

    // ---- deleteSubCategory ----
    @Test
    void deleteSubCategory_success_callsRepoDelete() {
        User user = new User();
        user.setUserId("deleter");
        SubCategory sc = new SubCategory();
        sc.setSubCategoryId(202L);
        sc.setCreatedBy(user);

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);

            when(subCategoryRepository.findByIdWithCreator(202L))
                    .thenReturn(Optional.of(sc));

            subCategoryService.deleteSubCategory(202L);
            verify(subCategoryRepository).delete(sc);
        }
    }

    @Test
    void deleteSubCategory_wrongOwner_throwsForbidden() {
        User user = new User();
        user.setUserId("u");
        User owner = new User();
        owner.setUserId("x");
        SubCategory sc = new SubCategory();
        sc.setCreatedBy(owner);

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);
            when(subCategoryRepository.findByIdWithCreator(anyLong())).thenReturn(Optional.of(sc));
            assertThrows(ForbiddenException.class, () -> subCategoryService.deleteSubCategory(3L));
            verify(subCategoryRepository, never()).delete(any());
        }
    }

    @Test
    void deleteSubCategory_notFound_throwsResourceNotFound() {
        User user = new User();
        user.setUserId("del");
        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);
            when(subCategoryRepository.findByIdWithCreator(55L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> subCategoryService.deleteSubCategory(55L));
            verify(subCategoryRepository, never()).delete(any());
        }
    }
}

