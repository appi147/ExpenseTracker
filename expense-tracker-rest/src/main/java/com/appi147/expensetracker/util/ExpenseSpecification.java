package com.appi147.expensetracker.util;

import com.appi147.expensetracker.entity.Expense;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class ExpenseSpecification {

    public static Specification<Expense> filter(
            String userId,
            Long categoryId,
            Long subCategoryId,
            String paymentTypeCode,
            LocalDate dateFrom,
            LocalDate dateTo
    ) {
        return (root, query, cb) -> {
            Predicate predicate = cb.equal(root.get("createdBy").get("id"), userId);

            if (categoryId != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("subCategory").get("category").get("categoryId"), categoryId)
                );
            }

            if (subCategoryId != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("subCategory").get("subCategoryId"), subCategoryId)
                );
            }

            if (paymentTypeCode != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("paymentType").get("code"), paymentTypeCode)
                );
            }

            if (dateFrom != null) {
                predicate = cb.and(predicate,
                        cb.greaterThanOrEqualTo(root.get("date"), dateFrom)
                );
            }

            if (dateTo != null) {
                predicate = cb.and(predicate,
                        cb.lessThanOrEqualTo(root.get("date"), dateTo)
                );
            }

            return predicate;
        };
    }
}
