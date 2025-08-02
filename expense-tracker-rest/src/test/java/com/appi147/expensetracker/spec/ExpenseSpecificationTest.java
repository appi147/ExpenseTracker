package com.appi147.expensetracker.spec;

import com.appi147.expensetracker.entity.*;
import com.appi147.expensetracker.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DataJpaTest
@ActiveProfiles("test")
class ExpenseSpecificationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ExpenseRepository expenseRepository;

    private User user;
    private Category category;
    private SubCategory subCategory;
    private PaymentType paymentType;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId("user123");
        entityManager.persist(user);

        category = new Category();
        category.setLabel("Food");
        category.setCreatedBy(user); // ✅ must match column constraint
        category.setCreatedAt(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()));
        category.setUpdatedAt(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()));
        entityManager.persist(category);

        subCategory = new SubCategory();
        subCategory.setCategory(category);
        subCategory.setLabel("Groceries");
        subCategory.setCreatedBy(user); // ✅ for audit columns
        subCategory.setCreatedAt(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()));
        subCategory.setUpdatedAt(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()));
        entityManager.persist(subCategory);

        paymentType = new PaymentType();
        paymentType.setCode("UPI");
        paymentType.setLabel("Unified Payments");
        paymentType.setCreatedBy(user); // ✅
        paymentType.setCreatedAt(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()));
        paymentType.setUpdatedAt(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()));
        entityManager.persist(paymentType);

        Expense e1 = new Expense();
        e1.setAmount(BigDecimal.valueOf(100));
        e1.setCreatedBy(user);
        e1.setSubCategory(subCategory);
        e1.setPaymentType(paymentType);
        e1.setDate(LocalDate.of(2024, 5, 20));
        e1.setCreatedAt(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()));
        e1.setUpdatedAt(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()));
        entityManager.persist(e1);

        Expense e2 = new Expense();
        e2.setAmount(BigDecimal.valueOf(500));
        e2.setCreatedBy(user);
        e2.setSubCategory(subCategory);
        e2.setPaymentType(paymentType);
        e2.setDate(LocalDate.of(2024, 6, 10));
        e2.setCreatedAt(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()));
        e2.setUpdatedAt(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()));
        entityManager.persist(e2);
    }


    @Test
    void filter_byUserId_shouldReturnAllUserExpenses() {
        var spec = ExpenseSpecification.filter("user123", null, null, null, null, null);
        var results = expenseRepository.findAll(spec);
        assertThat(results).hasSize(2);
    }

    @Test
    void filter_byCategory_shouldFilterCorrectly() {
        var spec = ExpenseSpecification.filter("user123", category.getCategoryId(), null, null, null, null);
        var results = expenseRepository.findAll(spec);
        assertThat(results).hasSize(2);
    }

    @Test
    void filter_bySubCategory_shouldFilterCorrectly() {
        var spec = ExpenseSpecification.filter("user123", null, subCategory.getSubCategoryId(), null, null, null);
        var results = expenseRepository.findAll(spec);
        assertThat(results).hasSize(2);
    }

    @Test
    void filter_byPaymentType_shouldFilterCorrectly() {
        var spec = ExpenseSpecification.filter("user123", null, null, "UPI", null, null);
        var results = expenseRepository.findAll(spec);
        assertThat(results).hasSize(2);
    }

    @Test
    void filter_byDateRange_shouldReturnOne() {
        var spec = ExpenseSpecification.filter("user123", null, null, null,
                LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 30));
        var results = expenseRepository.findAll(spec);
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getDate()).isEqualTo(LocalDate.of(2024, 6, 10));
    }

    @Test
    void filter_withAllFilters_shouldStillMatch() {
        var spec = ExpenseSpecification.filter("user123",
                category.getCategoryId(),
                subCategory.getSubCategoryId(),
                "UPI",
                LocalDate.of(2024, 5, 1),
                LocalDate.of(2024, 6, 30)
        );
        var results = expenseRepository.findAll(spec);
        assertThat(results).hasSize(2);
    }

    @Test
    void filter_withNonMatchingUser_shouldReturnEmpty() {
        var spec = ExpenseSpecification.filter("otherUser", null, null, null, null, null);
        var results = expenseRepository.findAll(spec);
        assertThat(results).isEmpty();
    }
}
