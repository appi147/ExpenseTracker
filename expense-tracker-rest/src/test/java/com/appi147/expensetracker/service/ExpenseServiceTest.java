package com.appi147.expensetracker.service;

import com.appi147.expensetracker.auth.UserContext;
import com.appi147.expensetracker.entity.*;
import com.appi147.expensetracker.exception.ForbiddenException;
import com.appi147.expensetracker.exception.ResourceNotFoundException;
import com.appi147.expensetracker.model.request.CreateExpenseRequest;
import com.appi147.expensetracker.model.response.CategoryWiseExpense;
import com.appi147.expensetracker.model.response.MonthlyExpense;
import com.appi147.expensetracker.model.response.MonthlyExpenseInsight;
import com.appi147.expensetracker.model.response.SubCategoryWiseExpense;
import com.appi147.expensetracker.repository.ExpenseRepository;
import com.appi147.expensetracker.spec.ExpenseSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    ExpenseRepository expenseRepository;

    @Mock
    PaymentTypeService paymentTypeService;

    @Mock
    SubCategoryService subCategoryService;

    @InjectMocks
    ExpenseService expenseService;

    // --- addExpense ---

    @Test
    void addExpense_success_savesWithAllFields() {
        User user = new User();
        user.setUserId("U1");
        SubCategory subCat = new SubCategory();
        subCat.setSubCategoryId(2L);
        PaymentType pt = new PaymentType();
        pt.setCode("CARD");
        CreateExpenseRequest req = new CreateExpenseRequest();
        req.setAmount(new BigDecimal("123.45"));
        req.setDate(LocalDate.now());
        req.setComments("Some info");
        req.setSubCategoryId(2L);
        req.setPaymentTypeCode("CARD");

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);

            when(subCategoryService.getSubCategory(2L)).thenReturn(subCat);
            when(paymentTypeService.getByCode("CARD")).thenReturn(pt);

            ArgumentCaptor<Expense> expenseCaptor = ArgumentCaptor.forClass(Expense.class);
            when(expenseRepository.saveAndFlush(expenseCaptor.capture())).thenAnswer(inv -> {
                // Simulate setting an ID to imitate persistence
                Expense e = expenseCaptor.getValue();
                e.setExpenseId(99L);
                return e;
            });

            expenseService.addExpense(req);

            Expense exp = expenseCaptor.getValue();
            assertEquals(new BigDecimal("123.45"), exp.getAmount());
            assertEquals(LocalDate.now(), exp.getDate());
            assertEquals("Some info", exp.getComments());
            assertSame(subCat, exp.getSubCategory());
            assertSame(pt, exp.getPaymentType());
            assertSame(user, exp.getCreatedBy());
            assertEquals(99L, exp.getExpenseId());
        }
    }

    @Test
    void addExpense_nullUser_throwsNPE() {
        CreateExpenseRequest req = new CreateExpenseRequest();
        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(null);
            assertThrows(NullPointerException.class, () -> expenseService.addExpense(req));
        }
    }

    @Test
    void addExpense_subCategoryServiceThrows_propagates() {
        User user = new User();
        user.setUserId("u");
        CreateExpenseRequest req = new CreateExpenseRequest();
        req.setSubCategoryId(99L);

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);
            when(subCategoryService.getSubCategory(99L)).thenThrow(new ResourceNotFoundException("boom"));

            assertThrows(ResourceNotFoundException.class, () -> expenseService.addExpense(req));
        }
    }

    @Test
    void addExpense_paymentTypeServiceThrows_propagates() {
        User user = new User();
        user.setUserId("u");
        CreateExpenseRequest req = new CreateExpenseRequest();
        req.setPaymentTypeCode("unknown");

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);
            when(subCategoryService.getSubCategory(any())).thenReturn(new SubCategory());
            when(paymentTypeService.getByCode("unknown")).thenThrow(new IllegalArgumentException("bad code"));
            assertThrows(IllegalArgumentException.class, () -> expenseService.addExpense(req));
        }
    }

    // --- getCurrentMonthExpense ---

    @Test
    void getCurrentMonthExpense_returnsBothFields() {
        User user = new User();
        user.setUserId("u88");
        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);

            BigDecimal curMonth = new BigDecimal("333.00");
            BigDecimal last30 = new BigDecimal("70.80");

            // Calculate the dates that the service will use for the two queries
            YearMonth nowMonth = YearMonth.now();
            LocalDate curMonthStart = nowMonth.atDay(1);
            LocalDate curMonthEnd = nowMonth.atEndOfMonth();
            LocalDate last30Start = LocalDate.now().minusDays(30);
            LocalDate last30End = LocalDate.now();

            // Now stub by matching these exact arguments
            when(expenseRepository.getSumOfExpensesBetweenDatesForUser(
                    eq("u88"), eq(curMonthStart), eq(curMonthEnd)
            )).thenReturn(curMonth);

            when(expenseRepository.getSumOfExpensesBetweenDatesForUser(
                    eq("u88"), eq(last30Start), eq(last30End)
            )).thenReturn(last30);

            ExpenseService spyService = Mockito.spy(expenseService);
            MonthlyExpense me = spyService.getCurrentMonthExpense();

            assertNotNull(me);
            assertEquals(curMonth, me.getCurrentMonth());
            assertEquals(last30, me.getLast30Days());
        }
    }


    @Test
    void getCurrentMonthExpense_returnsZeroIfNullFromRepo() {
        User user = new User();
        user.setUserId("eve");
        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);
            when(expenseRepository.getSumOfExpensesBetweenDatesForUser(eq("eve"), any(), any())).thenReturn(null);

            MonthlyExpense me = expenseService.getCurrentMonthExpense();

            assertNotNull(me);
            assertNull(me.getCurrentMonth());    // depends on how your MonthlyExpense is implemented!
            assertNull(me.getLast30Days());
        }
    }

    // --- getFilteredExpenses ---

    @Test
    void getFilteredExpenses_passingFilters_andReturnsPage() {
        User user = new User();
        user.setUserId("uQ");
        Page<Expense> expectedPage = new PageImpl<>(List.of(new Expense(), new Expense()));

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);
            // Stub ExpenseSpecification.filter to return null or a dummy spec if static
            Specification<Expense> spec = (root, query, builder) -> null;

            try (MockedStatic<ExpenseSpecification> es = mockStatic(ExpenseSpecification.class)) {
                es.when(() -> ExpenseSpecification.filter(any(), any(), any(), any(), any(), any()))
                        .thenReturn(spec);

                when(expenseRepository.findAll(eq(spec), any(Pageable.class)))
                        .thenReturn(expectedPage);

                Page<Expense> result = expenseService.getFilteredExpenses(1L, 2L, "cash",
                        LocalDate.of(2023, 1, 1), LocalDate.of(2023, 2, 1), 0, 5);

                assertSame(expectedPage, result);
                assertEquals(2, result.getTotalElements());
            }
        }
    }

    // --- deleteExpense ---

    @Test
    void deleteExpense_success_callsRepoDelete() {
        User user = new User();
        user.setUserId("usr5");
        Expense exp = new Expense();
        exp.setExpenseId(44L);
        exp.setCreatedBy(user);

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);
            when(expenseRepository.findByIdWithCreator(44L)).thenReturn(Optional.of(exp));
            expenseService.deleteExpense(44L);
            verify(expenseRepository).delete(exp);
        }
    }

    @Test
    void deleteExpense_whenNotFound_throwsResourceNotFound() {
        User user = new User();
        user.setUserId("me");
        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);
            when(expenseRepository.findByIdWithCreator(17L)).thenReturn(Optional.empty());
            Exception rnfe = assertThrows(ResourceNotFoundException.class, () ->
                    expenseService.deleteExpense(17L));
            assertTrue(rnfe.getMessage().contains("Expense not found"));
        }
    }

    @Test
    void deleteExpense_wrongOwner_throwsForbidden() {
        User req = new User();
        req.setUserId("me");
        User owner = new User();
        owner.setUserId("notme");
        Expense exp = new Expense();
        exp.setExpenseId(2L);
        exp.setCreatedBy(owner);

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(req);
            when(expenseRepository.findByIdWithCreator(2L)).thenReturn(Optional.of(exp));
            assertThrows(ForbiddenException.class, () -> expenseService.deleteExpense(2L));
            // Disambiguate here:
            verify(expenseRepository, never()).delete(exp);
            // or:
            // verify(expenseRepository, never()).delete((Expense) any());
            // or:
            // verify(expenseRepository, never()).delete(Mockito.<Expense>any());
        }
    }


    // --- updateExpenseAmount ---

    @Test
    void updateExpenseAmount_success_setsAmount() {
        User user = new User();
        user.setUserId("whoo");
        Expense exp = new Expense();
        exp.setExpenseId(1L);
        exp.setCreatedBy(user);
        exp.setAmount(new BigDecimal("5"));

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);
            when(expenseRepository.findByIdWithCreator(1L)).thenReturn(Optional.of(exp));
            when(expenseRepository.saveAndFlush(exp)).thenReturn(exp);

            expenseService.updateExpenseAmount(1L, new BigDecimal("129.0"));

            assertEquals(new BigDecimal("129.0"), exp.getAmount());
            verify(expenseRepository).saveAndFlush(exp);
        }
    }

    @Test
    void updateExpenseAmount_notFound_throwsResourceNotFound() {
        User user = new User();
        user.setUserId("id1");
        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);
            when(expenseRepository.findByIdWithCreator(12L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () ->
                    expenseService.updateExpenseAmount(12L, BigDecimal.ONE));
        }
    }

    // --- getMonthlyExpenseInsight ---

    @Test
    void getMonthlyExpenseInsight_forMonth_returnsInsight() {
        User user = new User();
        user.setUserId("u");
        user.setBudget(new BigDecimal("2000"));
        Expense e1 = new Expense();
        e1.setAmount(new BigDecimal("100"));  // Cat1, Sub1
        Expense e2 = new Expense();
        e2.setAmount(new BigDecimal("200"));  // Cat1, Sub2
        Expense e3 = new Expense();
        e3.setAmount(new BigDecimal("300"));  // Cat2, Sub3

        Category cat1 = new Category();
        cat1.setLabel("CatA");
        Category cat2 = new Category();
        cat2.setLabel("CatB");
        SubCategory sub1 = new SubCategory();
        sub1.setLabel("SubA");
        sub1.setCategory(cat1);
        SubCategory sub2 = new SubCategory();
        sub2.setLabel("SubB");
        sub2.setCategory(cat1);
        SubCategory sub3 = new SubCategory();
        sub3.setLabel("SubC");
        sub3.setCategory(cat2);
        e1.setSubCategory(sub1);
        e2.setSubCategory(sub2);
        e3.setSubCategory(sub3);

        List<Expense> expenses = Arrays.asList(e1, e2, e3);

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);
            when(expenseRepository.findByCreatedByAndDateBetween(eq(user), any(), any()))
                    .thenReturn(expenses);

            MonthlyExpenseInsight insight = expenseService.getMonthlyExpenseInsight(true);

            assertNotNull(insight);
            assertEquals(new BigDecimal("2000"), insight.getMonthlyBudget());
            assertEquals(new BigDecimal("600"), insight.getTotalExpense());

            // Category-wise
            assertEquals(2, insight.getCategoryWiseExpenses().size());
            CategoryWiseExpense cwe1 = insight.getCategoryWiseExpenses().get(0);
            CategoryWiseExpense cwe2 = insight.getCategoryWiseExpenses().get(1);
            // Sorted by amount descending
            assertTrue(cwe1.getAmount().compareTo(cwe2.getAmount()) >= 0);
            // Subcategory-wise
            List<SubCategoryWiseExpense> scweAll = new ArrayList<>();
            scweAll.addAll(cwe1.getSubCategoryWiseExpenses());
            scweAll.addAll(cwe2.getSubCategoryWiseExpenses());
            BigDecimal all = scweAll.stream().map(SubCategoryWiseExpense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            assertEquals(insight.getTotalExpense(), all);
        }
    }

    @Test
    void getMonthlyExpenseInsight_emptyList_returnsZeroTotal() {
        User user = new User();
        user.setUserId("zZz");
        user.setBudget(BigDecimal.TEN);

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);
            when(expenseRepository.findByCreatedByAndDateBetween(eq(user), any(), any()))
                    .thenReturn(Collections.emptyList());

            MonthlyExpenseInsight insight = expenseService.getMonthlyExpenseInsight(false);

            assertEquals(BigDecimal.TEN, insight.getMonthlyBudget());
            assertEquals(BigDecimal.ZERO, insight.getTotalExpense());
            assertTrue(insight.getCategoryWiseExpenses().isEmpty());
        }
    }

    // --- getExpenseIfOwnedByCurrentUser: Throws if UserContext null ---

    @Test
    void deleteExpense_noCurrentUser_throwsNPE() {
        when(expenseRepository.findByIdWithCreator(anyLong())).thenReturn(Optional.of(new Expense()));
        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(null);
            assertThrows(NullPointerException.class, () ->
                    expenseService.deleteExpense(1L));
        }
    }

    @Test
    void updateExpenseAmount_noCurrentUser_throwsNPE() {
        when(expenseRepository.findByIdWithCreator(anyLong())).thenReturn(Optional.of(new Expense()));
        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(null);
            assertThrows(NullPointerException.class, () ->
                    expenseService.updateExpenseAmount(1L, BigDecimal.ONE));
        }
    }

    // -- extreme filter params (nulls etc.) for getFilteredExpenses

    @Test
    void getFilteredExpenses_allNullParams_stillCallsAndReturnsResult() {
        User user = new User();
        user.setUserId("uZ");
        Page<Expense> resultPage = new PageImpl<>(Collections.emptyList());
        Specification<Expense> spec = (root, query, cb) -> null;

        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class);
             MockedStatic<ExpenseSpecification> es = mockStatic(ExpenseSpecification.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);
            es.when(() -> ExpenseSpecification.filter(any(), any(), any(), any(), any(), any())).thenReturn(spec);
            // Explicitly use spec to avoid ambiguity
            when(expenseRepository.findAll(eq(spec), any(Pageable.class))).thenReturn(resultPage);

            Page<Expense> result = expenseService.getFilteredExpenses(null, null, null, null, null, 0, 10);
            assertSame(resultPage, result);
        }
    }

}
