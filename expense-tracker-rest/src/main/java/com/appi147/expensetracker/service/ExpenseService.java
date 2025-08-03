package com.appi147.expensetracker.service;

import com.appi147.expensetracker.auth.UserContext;
import com.appi147.expensetracker.entity.Expense;
import com.appi147.expensetracker.entity.PaymentType;
import com.appi147.expensetracker.entity.SubCategory;
import com.appi147.expensetracker.entity.User;
import com.appi147.expensetracker.exception.ForbiddenException;
import com.appi147.expensetracker.exception.ResourceNotFoundException;
import com.appi147.expensetracker.model.request.ExpenseCreateRequest;
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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
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

    public void addExpense(ExpenseCreateRequest request) {
        User user = UserContext.getCurrentUser();
        log.info("[ExpenseService] User [{}] adding expense: amount={}, date={}, subCategoryId={}, paymentTypeCode={}, monthsToAmortize={}",
                user.getUserId(), request.getAmount(), request.getDate(), request.getSubCategoryId(), request.getPaymentTypeCode(),
                request.getMonthsToAmortize());

        SubCategory subCategory = subCategoryService.getSubCategory(request.getSubCategoryId());
        PaymentType paymentType = paymentTypeService.getByCode(request.getPaymentTypeCode());

        if (request.getMonthsToAmortize().getMonths() > 1) {
            List<Expense> amortizedExpenses = createAmortizedExpenses(request, subCategory, paymentType, user);
            expenseRepository.saveAllAndFlush(amortizedExpenses);
            log.info("[ExpenseService] {} amortized expenses created for user {}", amortizedExpenses.size(), user.getUserId());
        } else {
            Expense expense = new Expense();
            expense.setAmount(request.getAmount());
            expense.setDate(request.getDate());
            expense.setComments(request.getComments());
            expense.setSubCategory(subCategory);
            expense.setPaymentType(paymentType);
            expense.setCreatedBy(user);

            expenseRepository.saveAndFlush(expense);
            log.info("[ExpenseService] Expense created: id={}, userId={}", expense.getExpenseId(), user.getUserId());
        }
    }

    private List<Expense> createAmortizedExpenses(
            ExpenseCreateRequest request, SubCategory subCategory,
            PaymentType paymentType, User user
    ) {
        long months = request.getMonthsToAmortize().getMonths();
        BigDecimal totalAmount = request.getAmount();
        BigDecimal monthlyAmount = totalAmount.divide(BigDecimal.valueOf(months), 2, RoundingMode.DOWN);
        BigDecimal lastMonthAmount = totalAmount.subtract(monthlyAmount.multiply(BigDecimal.valueOf(months - 1)));

        List<Expense> expenses = new ArrayList<>();

        LocalDate startDate = request.getDate();
        for (int i = 0; i < months; i++) {
            LocalDate date = adjustToValidDate(startDate.plusMonths(i));

            Expense expense = new Expense();
            expense.setAmount(i == months - 1 ? lastMonthAmount : monthlyAmount);
            expense.setDate(date);
            expense.setComments(request.getComments() + " (Part " + (i + 1) + "/" + months + ")");
            expense.setSubCategory(subCategory);
            expense.setPaymentType(paymentType);
            expense.setCreatedBy(user);

            expenses.add(expense);
        }

        return expenses;
    }

    private LocalDate adjustToValidDate(LocalDate date) {
        int lastDay = YearMonth.of(date.getYear(), date.getMonth()).lengthOfMonth();
        int day = Math.min(date.getDayOfMonth(), lastDay);
        return LocalDate.of(date.getYear(), date.getMonth(), day);
    }

    public MonthlyExpense getCurrentMonthExpense() {
        String userId = UserContext.getCurrentUser().getUserId();
        log.info("[ExpenseService] Fetching current month expense summary for userId={}", userId);

        BigDecimal currentMonthExpense = getMonthlyExpenseSumForUser(userId, YearMonth.now());
        BigDecimal last30DaysExpense = getExpenseSumInPeriodForUser(userId, LocalDate.now().minusDays(30), LocalDate.now());
        MonthlyExpense monthlyExpense = new MonthlyExpense(last30DaysExpense, currentMonthExpense);
        log.debug("[ExpenseService] MonthlyExpense for userId={}: currentMonth={}, last30Days={}",
                userId, currentMonthExpense, last30DaysExpense);
        return monthlyExpense;
    }

    private BigDecimal getMonthlyExpenseSumForUser(String userId, YearMonth month) {
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        log.debug("[ExpenseService] Summing expenses for userId={} between {} and {}", userId, start, end);
        return expenseRepository.getSumOfExpensesBetweenDatesForUser(userId, start, end);
    }

    private BigDecimal getExpenseSumInPeriodForUser(String userId, LocalDate from, LocalDate to) {
        log.debug("[ExpenseService] Summing expenses for userId={} from {} to {}", userId, from, to);
        return expenseRepository.getSumOfExpensesBetweenDatesForUser(userId, from, to);
    }

    public Page<Expense> getFilteredExpenses(Long categoryId, Long subCategoryId, String paymentTypeCode,
                                             LocalDate dateFrom, LocalDate dateTo, int page, int size) {
        String userId = UserContext.getCurrentUser().getUserId();
        log.info("[ExpenseService] Filtering expenses for userId={}, categoryId={}, subCategoryId={}, paymentTypeCode={}, dateFrom={}, dateTo={}, page={}, size={}",
                userId, categoryId, subCategoryId, paymentTypeCode, dateFrom, dateTo, page, size);
        Specification<Expense> spec = ExpenseSpecification.filter(
                userId, categoryId, subCategoryId, paymentTypeCode, dateFrom, dateTo
        );
        Pageable pageable = PageRequest.of(page, size, Sort.by("date", "updatedAt").descending());
        Page<Expense> result = expenseRepository.findAll(spec, pageable);
        log.info("[ExpenseService] Filtered expenses found: count={} for userId={}", result.getTotalElements(), userId);
        return result;
    }

    public void deleteExpense(Long expenseId) {
        Expense expense = getExpenseIfOwnedByCurrentUser(expenseId);
        log.info("[ExpenseService] Deleting expense [{}] by user [{}]", expenseId, expense.getCreatedBy().getUserId());
        expenseRepository.delete(expense);
        log.info("[ExpenseService] Expense [{}] deleted by user [{}]", expenseId, expense.getCreatedBy().getUserId());
    }

    private Expense getExpenseIfOwnedByCurrentUser(Long expenseId) {
        User requester = UserContext.getCurrentUser();
        Expense expense = expenseRepository.findByIdWithCreator(expenseId)
                .orElseThrow(() -> {
                    log.warn("[ExpenseService] Expense [{}] not found for user [{}]", expenseId, requester.getUserId());
                    return new ResourceNotFoundException("Expense not found with id " + expenseId);
                });

        if (expense.getCreatedBy() == null || !expense.getCreatedBy().getUserId().equals(requester.getUserId())) {
            log.warn("[ExpenseService] Access denied: user [{}] tried to access expense [{}] not owned by them", requester.getUserId(), expenseId);
            throw new ForbiddenException("You are not allowed to access this resource");
        }
        log.debug("[ExpenseService] Expense [{}] accessed by owner [{}]", expenseId, requester.getUserId());
        return expense;
    }

    public void updateExpenseAmount(Long expenseId, BigDecimal amount) {
        Expense expense = getExpenseIfOwnedByCurrentUser(expenseId);
        BigDecimal previousAmount = expense.getAmount();
        expense.setAmount(amount);
        expenseRepository.saveAndFlush(expense);
        log.info("[ExpenseService] Expense [{}] amount updated from {} to {} by user [{}]", expenseId, previousAmount, amount, expense.getCreatedBy().getUserId());
    }

    public MonthlyExpenseInsight getMonthlyExpenseInsight(boolean monthly) {
        User user = UserContext.getCurrentUser();

        LocalDate start, end;
        if (monthly) {
            YearMonth month = YearMonth.now();
            start = month.atDay(1);
            end = month.atEndOfMonth();
            log.info("[ExpenseService] Getting category insight for user [{}] for current month [{}-{}]", user.getUserId(), start, end);
        } else {
            start = LocalDate.now().minusDays(30);
            end = LocalDate.now();
            log.info("[ExpenseService] Getting category insight for user [{}] for the last 30 days [{}-{}]", user.getUserId(), start, end);
        }

        List<Expense> expenseList = expenseRepository.findByCreatedByAndDateBetween(user, start, end);

        log.debug("[ExpenseService] Found {} expenses in period for user [{}]", expenseList.size(), user.getUserId());

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
                            .sorted(Comparator.comparing(SubCategoryWiseExpense::amount, Comparator.reverseOrder()))
                            .toList();

                    BigDecimal categoryTotal = subCategoryWiseExpenses.stream()
                            .map(SubCategoryWiseExpense::amount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    log.debug("[ExpenseService] Category [{}] total={} for user [{}]", category, categoryTotal, user.getUserId());

                    return new CategoryWiseExpense(category, categoryTotal, subCategoryWiseExpenses);
                })
                .sorted(Comparator.comparing(CategoryWiseExpense::amount, Comparator.reverseOrder()))
                .toList();

        BigDecimal totalAmount = categoryWiseExpenses.stream()
                .map(CategoryWiseExpense::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("[ExpenseService] MonthlyExpenseInsight: user [{}], budget={}, periodTotal={}", user.getUserId(), user.getBudget(), totalAmount);

        return new MonthlyExpenseInsight(user.getBudget(), totalAmount, categoryWiseExpenses);
    }

}
