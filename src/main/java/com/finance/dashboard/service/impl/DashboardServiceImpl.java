package com.finance.dashboard.service.impl;

import com.finance.dashboard.dto.CategorySummaryDTO;
import com.finance.dashboard.dto.DashboardSummaryDTO;
import com.finance.dashboard.dto.MonthlySummaryDTO;
import com.finance.dashboard.repository.FinancialRecordRepository;
import com.finance.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final FinancialRecordRepository repository;

    @Cacheable("dashboardSummary")
    @Override
    public DashboardSummaryDTO getSummary() {
        BigDecimal income = repository.getTotalIncome();
        BigDecimal expense = repository.getTotalExpense();
        return new DashboardSummaryDTO(income, expense, income.subtract(expense));
    }
    @Cacheable("categorySummary")
    @Override
    public List<CategorySummaryDTO> getCategorySummary() {

        return repository.getCategoryTotals()
                .stream()
                .map(obj -> new CategorySummaryDTO(
                        (String) obj[0],
                        (BigDecimal) obj[1]
                ))
                .toList();
    }
    @Cacheable("monthlySummary")
    @Override
    public List<MonthlySummaryDTO> getMonthlySummary() {
        // Query returns: [year, month, type (String), sum]
        List<Object[]> rows = repository.getMonthlyTotals();

        java.util.LinkedHashMap<String, MonthlySummaryDTO> map = new java.util.LinkedHashMap<>();
        rows.forEach(row -> updateMonthlyMap(map, row));
        
        return new java.util.ArrayList<>(map.values());
    }

    private void updateMonthlyMap(java.util.Map<String, MonthlySummaryDTO> map, Object[] row) {
        Integer year  = (Integer) row[0];
        Integer month = (Integer) row[1];
        String  type  = row[2].toString();
        java.math.BigDecimal amount = (java.math.BigDecimal) row[3];

        String key = year + "-" + month;
        MonthlySummaryDTO dto = map.computeIfAbsent(key, k -> new MonthlySummaryDTO(year, month));
        
        if ("INCOME".equals(type)) {
            dto.setIncome(amount);
        } else {
            dto.setExpense(amount);
        }
    }
}