package com.appi147.expensetracker.scheduler;

import com.appi147.expensetracker.service.RecurringExpenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class RecurringExpenseSchedulerTest {

    private RecurringExpenseService recurringExpenseService;
    private RecurringExpenseScheduler scheduler;

    @BeforeEach
    void setUp() {
        recurringExpenseService = Mockito.mock(RecurringExpenseService.class);
        scheduler = new RecurringExpenseScheduler(recurringExpenseService);
    }

    @Test
    void createMonthlyRecurringExpenses_shouldCallService() {
        // Act
        scheduler.createMonthlyRecurringExpenses();

        // Assert
        Mockito.verify(recurringExpenseService, Mockito.times(1)).createThisMonthExpenses();
    }
}
