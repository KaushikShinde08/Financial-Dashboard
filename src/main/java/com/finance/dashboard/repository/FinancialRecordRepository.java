package com.finance.dashboard.repository;


import com.finance.dashboard.entity.FinancialRecord;
import com.finance.dashboard.entity.RecordType;
import com.finance.dashboard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

    List<FinancialRecord> findByType(RecordType type);

    List<FinancialRecord> findByCategory(String category);

    List<FinancialRecord> findByDateBetween(LocalDate start, LocalDate end);

    List<FinancialRecord> findByCreatedBy(User user);

    @Query("SELECT COALESCE(SUM(f.amount), 0) FROM FinancialRecord f WHERE f.type = 'INCOME'")
    BigDecimal getTotalIncome();

    @Query("SELECT COALESCE(SUM(f.amount), 0) FROM FinancialRecord f WHERE f.type = 'EXPENSE'")
    BigDecimal getTotalExpense();

    @Query("""
       SELECT f.category, SUM(f.amount)
       FROM FinancialRecord f
       GROUP BY f.category
       """)
    List<Object[]> getCategoryTotals();

    @Query("""
       SELECT FUNCTION('YEAR', f.date), FUNCTION('MONTH', f.date), SUM(f.amount)
       FROM FinancialRecord f
       GROUP BY FUNCTION('YEAR', f.date), FUNCTION('MONTH', f.date)
       ORDER BY FUNCTION('YEAR', f.date), FUNCTION('MONTH', f.date)
       """)
    List<Object[]> getMonthlyTotals();

    @Query("""
       SELECT f FROM FinancialRecord f
       WHERE (:type IS NULL OR f.type = :type)
       AND (:category IS NULL OR f.category = :category)
       AND (:startDate IS NULL OR f.date >= :startDate)
       AND (:endDate IS NULL OR f.date <= :endDate)
       """)
    List<FinancialRecord> filterRecords(
            @Param("type") RecordType type,
            @Param("category") String category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
