package com.appi147.expensetracker.repository;

import com.appi147.expensetracker.entity.User;
import com.appi147.expensetracker.model.response.SiteWideInsight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InsightRepository extends JpaRepository<User, Long> {

    @Query("""
              SELECT new com.appi147.expensetracker.model.response.SiteWideInsight(
                (SELECT COUNT(u) FROM User u),
                (SELECT COUNT(DISTINCT e.createdBy.userId) FROM Expense e),
                (SELECT COALESCE(SUM(e.amount), 0) FROM Expense e),
                (SELECT COUNT(e) FROM Expense e),
                (SELECT COUNT(c) FROM Category c),
                (SELECT COUNT(sc) FROM SubCategory sc)
              )
              FROM User u
              WHERE u.id = (SELECT MIN(u2.id) FROM User u2)
            """)
    SiteWideInsight getSiteWideInsight();
}
