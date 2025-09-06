package com.appi147.expensetracker.service;

import com.appi147.expensetracker.auth.UserContext;
import com.appi147.expensetracker.entity.*;
import com.appi147.expensetracker.exception.ForbiddenException;
import com.appi147.expensetracker.exception.ResourceNotFoundException;
import com.appi147.expensetracker.model.request.RecurringExpenseCreateRequest;
import com.appi147.expensetracker.repository.ExpenseRepository;
import com.appi147.expensetracker.repository.RecurringExpenseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecurringExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private RecurringExpenseRepository recurringExpenseRepository;

    @Mock
    private PaymentTypeService paymentTypeService;

    @Mock
    private SubCategoryService subCategoryService;

    @InjectMocks
    private RecurringExpenseService recurringExpenseService;

    // --- addRecurringExpense ---

    @Test
    void addRecurringExpense_success() {
        User user = new User();
        user.setUserId("U1");
        SubCategory sub = new SubCategory();
        sub.setSubCategoryId(2L);
        PaymentType pt = new PaymentType();
        pt.setCode("CASH");

        RecurringExpenseCreateRequest req = new RecurringExpenseCreateRequest();
        req.setAmount(BigDecimal.valueOf(100));
        req.setDayOfMonth(15);
        req.setSubCategoryId(2L);
        req.setPaymentTypeCode("CASH");
        req.setComments("Test comment");

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);
            when(subCategoryService.getSubCategory(2L)).thenReturn(sub);
            when(paymentTypeService.getByCode("CASH")).thenReturn(pt);

            ArgumentCaptor<RecurringExpense> captor = ArgumentCaptor.forClass(RecurringExpense.class);
            recurringExpenseService.addRecurringExpense(req);

            verify(recurringExpenseRepository).saveAndFlush(captor.capture());
            RecurringExpense saved = captor.getValue();
            assertEquals(BigDecimal.valueOf(100), saved.getAmount());
            assertEquals(15, saved.getDayOfMonth());
            assertEquals(sub, saved.getSubCategory());
            assertEquals(pt, saved.getPaymentType());
            assertEquals("Test comment", saved.getComments());
            assertEquals(user, saved.getCreatedBy());
        }
    }

    @Test
    void addRecurringExpense_invalidDay_throwsBadRequest() {
        RecurringExpenseCreateRequest req = new RecurringExpenseCreateRequest();
        req.setAmount(BigDecimal.valueOf(100));
        req.setDayOfMonth(30); // invalid
        req.setSubCategoryId(1L);
        req.setPaymentTypeCode("CASH");

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(new User());
            assertThrows(RuntimeException.class, () -> recurringExpenseService.addRecurringExpense(req));
            verify(recurringExpenseRepository, never()).saveAndFlush(any());
        }
    }

    @Test
    void addRecurringExpense_invalidDayZero_throwsBadRequest() {
        RecurringExpenseCreateRequest req = new RecurringExpenseCreateRequest();
        req.setAmount(BigDecimal.valueOf(100));
        req.setDayOfMonth(0); // invalid
        req.setSubCategoryId(1L);
        req.setPaymentTypeCode("CASH");

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(new User());
            assertThrows(RuntimeException.class, () -> recurringExpenseService.addRecurringExpense(req));
            verify(recurringExpenseRepository, never()).saveAndFlush(any());
        }
    }

    // --- deleteRecurringExpense ---

    @Test
    void deleteRecurringExpense_success() {
        User user = new User();
        user.setUserId("u1");

        RecurringExpense re = new RecurringExpense();
        re.setRecurringExpenseId(1L);
        re.setCreatedBy(user);

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);
            when(recurringExpenseRepository.findById(1L)).thenReturn(Optional.of(re));

            recurringExpenseService.deleteRecurringExpense(1L);
            verify(recurringExpenseRepository).delete(re);
        }
    }

    @Test
    void deleteRecurringExpense_notFound_throwsResourceNotFound() {
        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(new User());
            when(recurringExpenseRepository.findById(1L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> recurringExpenseService.deleteRecurringExpense(1L));
            verify(recurringExpenseRepository, never()).delete(any());
        }
    }

    @Test
    void deleteRecurringExpense_wrongOwner_throwsForbidden() {
        User current = new User();
        current.setUserId("me");
        User owner = new User();
        owner.setUserId("notme");

        RecurringExpense re = new RecurringExpense();
        re.setRecurringExpenseId(1L);
        re.setCreatedBy(owner);

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(current);
            when(recurringExpenseRepository.findById(1L)).thenReturn(Optional.of(re));
            assertThrows(ForbiddenException.class, () -> recurringExpenseService.deleteRecurringExpense(1L));
            verify(recurringExpenseRepository, never()).delete(any());
        }
    }

    // --- listRecurringExpensesForCurrentUser ---

    @Test
    void listRecurringExpenses_returnsAllForUser() {
        User user = new User();
        user.setUserId("u1");

        RecurringExpense r1 = new RecurringExpense();
        r1.setCreatedBy(user);
        RecurringExpense r2 = new RecurringExpense();
        r2.setCreatedBy(user);

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);
            when(recurringExpenseRepository.findByCreatedBy(user)).thenReturn(List.of(r1, r2));

            List<RecurringExpense> result = recurringExpenseService.listRecurringExpensesForCurrentUser();
            assertEquals(2, result.size());
            assertTrue(result.contains(r1));
            assertTrue(result.contains(r2));
        }
    }

    @Test
    void createThisMonthExpenses_shouldSaveExpenses() {
        // Arrange
        User user = new User();
        SubCategory subCategory = new SubCategory();
        PaymentType paymentType = new PaymentType();

        RecurringExpense r1 = new RecurringExpense();
        r1.setAmount(new BigDecimal("100.50"));
        r1.setComments("Monthly subscription");
        r1.setCreatedBy(user);
        r1.setSubCategory(subCategory);
        r1.setPaymentType(paymentType);

        when(recurringExpenseRepository.findByDayOfMonth(LocalDate.now().getDayOfMonth()))
                .thenReturn(List.of(r1));

        // Act
        recurringExpenseService.createThisMonthExpenses();

        // Assert
        ArgumentCaptor<Expense> captor = ArgumentCaptor.forClass(Expense.class);
        verify(expenseRepository, times(1)).save(captor.capture());

        Expense saved = captor.getValue();
        assertThat(saved.getAmount()).isEqualTo(new BigDecimal("100.50"));
        assertThat(saved.getSubCategory()).isSameAs(subCategory);
        assertThat(saved.getPaymentType()).isSameAs(paymentType);
        assertThat(saved.getCreatedBy()).isSameAs(user);
        assertThat(saved.getComments()).isEqualTo("Monthly subscriptionAdded by Recurring");
        assertThat(saved.getDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void createThisMonthExpenses_emptyList_shouldNotSaveAnything() {
        // Arrange
        when(recurringExpenseRepository.findByDayOfMonth(LocalDate.now().getDayOfMonth()))
                .thenReturn(List.of());

        // Act
        recurringExpenseService.createThisMonthExpenses();

        // Assert
        verify(expenseRepository, never()).save(any());
    }
}
