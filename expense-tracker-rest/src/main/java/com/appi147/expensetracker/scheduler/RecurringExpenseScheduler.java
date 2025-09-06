package com.appi147.expensetracker.scheduler;

import com.appi147.expensetracker.service.RecurringExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecurringExpenseScheduler {

    private final RecurringExpenseService recurringExpenseService;

    /**
     * Runs every day at 12:01 AM to create monthly expenses
     * based on recurring templates.
     */
    @Scheduled(cron = "0 1 0 * * ?")
    public void createMonthlyRecurringExpenses() {
        recurringExpenseService.createThisMonthExpenses();
    }
}
