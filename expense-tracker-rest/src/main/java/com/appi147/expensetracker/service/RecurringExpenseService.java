package com.appi147.expensetracker.service;

import com.appi147.expensetracker.auth.UserContext;
import com.appi147.expensetracker.entity.PaymentType;
import com.appi147.expensetracker.entity.RecurringExpense;
import com.appi147.expensetracker.entity.SubCategory;
import com.appi147.expensetracker.entity.User;
import com.appi147.expensetracker.exception.BadRequestException;
import com.appi147.expensetracker.exception.ForbiddenException;
import com.appi147.expensetracker.exception.ResourceNotFoundException;
import com.appi147.expensetracker.model.request.RecurringExpenseCreateRequest;
import com.appi147.expensetracker.repository.RecurringExpenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecurringExpenseService {

    private final RecurringExpenseRepository recurringExpenseRepository;
    private final PaymentTypeService paymentTypeService;
    private final SubCategoryService subCategoryService;

    /**
     * Adds a new recurring expense for the current user.
     */
    public void addRecurringExpense(RecurringExpenseCreateRequest request) {
        User user = UserContext.getCurrentUser();
        log.info("[ExpenseService] User [{}] adding recurring expense: amount={}, dayOfMonth={}, subCategoryId={}, paymentTypeCode={}",
                user.getUserId(), request.getAmount(), request.getDayOfMonth(), request.getSubCategoryId(), request.getPaymentTypeCode());

        if (request.getDayOfMonth() < 1 || request.getDayOfMonth() > 28) {
            throw new BadRequestException("Day of month must be between 1 and 28");
        }

        SubCategory subCategory = subCategoryService.getSubCategory(request.getSubCategoryId());
        PaymentType paymentType = paymentTypeService.getByCode(request.getPaymentTypeCode());

        RecurringExpense recurringExpense = new RecurringExpense();
        recurringExpense.setAmount(request.getAmount());
        recurringExpense.setComments(request.getComments());
        recurringExpense.setDayOfMonth(request.getDayOfMonth());
        recurringExpense.setSubCategory(subCategory);
        recurringExpense.setPaymentType(paymentType);
        recurringExpense.setCreatedBy(user);

        recurringExpenseRepository.saveAndFlush(recurringExpense);
    }

    /**
     * Deletes a recurring expense by ID for the current user.
     */
    public void deleteRecurringExpense(Long id) {
        User user = UserContext.getCurrentUser();
        RecurringExpense expense = recurringExpenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recurring expense not found with id " + id));

        if (!expense.getCreatedBy().getUserId().equals(user.getUserId())) {
            throw new ForbiddenException("You are not allowed to delete this recurring expense");
        }

        recurringExpenseRepository.delete(expense);
        log.info("[RecurringExpenseService] Recurring expense deleted successfully: id={}, user={}", id, user.getUserId());
    }

    /**
     * Returns all recurring expenses for the current user.
     */
    public List<RecurringExpense> listRecurringExpensesForCurrentUser() {
        User user = UserContext.getCurrentUser();
        return recurringExpenseRepository.findByCreatedBy(user);
    }
}
