package com.appi147.expensetracker.service;

import com.appi147.expensetracker.auth.UserContext;
import com.appi147.expensetracker.entity.Expense;
import com.appi147.expensetracker.entity.PaymentType;
import com.appi147.expensetracker.entity.SubCategory;
import com.appi147.expensetracker.entity.User;
import com.appi147.expensetracker.exception.ForbiddenException;
import com.appi147.expensetracker.exception.ResourceNotFoundException;
import com.appi147.expensetracker.model.request.CreateExpenseRequest;
import com.appi147.expensetracker.model.response.CategoryWiseExpense;
import com.appi147.expensetracker.model.response.MonthlyExpense;
import com.appi147.expensetracker.model.response.MonthlyExpenseInsight;
import com.appi147.expensetracker.model.response.SubCategoryWiseExpense;
import com.appi147.expensetracker.repository.ExpenseRepository;
import com.appi147.expensetracker.spec.ExpenseSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
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
        Pageable pageable = PageRequest.of(page, size, Sort.by("date", "updatedAt").descending());
        return expenseRepository.findAll(spec, pageable);
    }

    public void deleteExpense(Long expenseId) {
        Expense expense = getExpenseIfOwnedByCurrentUser(expenseId);
        log.info("Deleting expense [{}] by user [{}]", expenseId, expense.getCreatedBy().getUserId());
        expenseRepository.delete(expense);
    }

    private Expense getExpenseIfOwnedByCurrentUser(Long expenseId) {
        User requester = UserContext.getCurrentUser();
        Expense expense = expenseRepository.findByIdWithCreator(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id " + expenseId));

        if (expense.getCreatedBy() == null || !expense.getCreatedBy().getUserId().equals(requester.getUserId())) {
            log.warn("User [{}] tried to access expense [{}] not owned by them", requester.getUserId(), expenseId);
            throw new ForbiddenException("You are not allowed to access this resource");
        }

        return expense;
    }

    public void updateExpenseAmount(Long expenseId, BigDecimal amount) {
        Expense expense = getExpenseIfOwnedByCurrentUser(expenseId);
        expense.setAmount(amount);
        expenseRepository.saveAndFlush(expense);
    }

    public MonthlyExpenseInsight getMonthlyExpenseInsight(boolean monthly) {
        User user = UserContext.getCurrentUser();
        LocalDate start, end;

        if (monthly) {
            YearMonth month = YearMonth.now();
            start = month.atDay(1);
            end = month.atEndOfMonth();
        } else {
            start = LocalDate.now().minusDays(30);
            end = LocalDate.now();
        }

        List<Expense> expenseList = expenseRepository.findByCreatedByAndDateBetween(user, start, end);

        List<CategoryWiseExpense> categoryWiseExpenses = expenseList.stream()
                .collect(Collectors.groupingBy(e -> e.getSubCategory().getCategory().getLabel()))
                .entrySet()
                .stream()
                .map(categoryEntry -> {
                    String category = categoryEntry.getKey();
                    Map<String, BigDecimal> subCategorySums = categoryEntry.getValue().stream()
                            .collect(Collectors.groupingBy(
                                    e -> e.getSubCategory().getLabel(),
                                    Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)
                            ));

                    List<SubCategoryWiseExpense> subCategoryWiseExpenses = subCategorySums.entrySet().stream()
                            .map(e -> new SubCategoryWiseExpense(e.getKey(), e.getValue()))
                            .sorted(Comparator.comparing(SubCategoryWiseExpense::getAmount, Comparator.reverseOrder()))
                            .toList();

                    BigDecimal categoryTotal = subCategoryWiseExpenses.stream()
                            .map(SubCategoryWiseExpense::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return new CategoryWiseExpense(category, categoryTotal, subCategoryWiseExpenses);
                })
                .sorted(Comparator.comparing(CategoryWiseExpense::getAmount, Comparator.reverseOrder()))
                .toList();

        BigDecimal totalAmount = categoryWiseExpenses.stream()
                .map(CategoryWiseExpense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new MonthlyExpenseInsight(user.getBudget(), totalAmount, categoryWiseExpenses);
    }

}
