package com.appi147.expensetracker.service;

import com.appi147.expensetracker.auth.UserContext;
import com.appi147.expensetracker.entity.Expense;
import com.appi147.expensetracker.entity.PaymentType;
import com.appi147.expensetracker.entity.SubCategory;
import com.appi147.expensetracker.entity.User;
import com.appi147.expensetracker.model.request.CreateExpenseRequest;
import com.appi147.expensetracker.model.response.MonthlyExpense;
import com.appi147.expensetracker.repository.ExpenseRepository;
import com.appi147.expensetracker.util.ExpenseSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final PaymentTypeService paymentTypeService;
    private final SubCategoryService subCategoryService;

    public void addExpense(CreateExpenseRequest request) {
        User user = UserContext.getCurrentUser();

        SubCategory subCategory = subCategoryService.getSubCategory(request.getSubCategoryId());
        PaymentType paymentType = paymentTypeService.getByCode(request.getPaymentTypeCode());

        Expense expense = new Expense();
        expense.setAmount(request.getAmount());
        expense.setDate(request.getDate());
        expense.setComments(request.getComments());
        expense.setSubCategory(subCategory);
        expense.setPaymentType(paymentType);
        expense.setCreatedBy(user);

        expenseRepository.saveAndFlush(expense);
    }

    public MonthlyExpense getCurrentMonthExpense() {
        String userId = UserContext.getCurrentUser().getUserId();
        MonthlyExpense monthlyExpense = new MonthlyExpense();
        BigDecimal currentMonthExpense = getMonthlyExpenseSumForUser(userId, YearMonth.now());
        BigDecimal last30DaysExpense = getExpenseSumInPeriodForUser(userId, LocalDate.now().minusDays(30), LocalDate.now());
        monthlyExpense.setCurrentMonth(currentMonthExpense);
        monthlyExpense.setLast30Days(last30DaysExpense);
        return monthlyExpense;
    }

    private BigDecimal getMonthlyExpenseSumForUser(String userId, YearMonth month) {
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        return expenseRepository.getSumOfExpensesBetweenDatesForUser(userId, start, end);
    }

    private BigDecimal getExpenseSumInPeriodForUser(String userId, LocalDate from, LocalDate to) {
        return expenseRepository.getSumOfExpensesBetweenDatesForUser(userId, from, to);
    }

    public Page<Expense> getFilteredExpenses(Long categoryId, Long subCategoryId, String paymentTypeCode,
                                             LocalDate dateFrom, LocalDate dateTo, int page, int size) {
        String userId = UserContext.getCurrentUser().getUserId();
        Specification<Expense> spec = ExpenseSpecification.filter(
                userId, categoryId, subCategoryId, paymentTypeCode, dateFrom, dateTo
        );
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        return expenseRepository.findAll(spec, pageable);
    }
}
