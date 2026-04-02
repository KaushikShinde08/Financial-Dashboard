package com.finance.dashboard.service.impl;

import com.finance.dashboard.dto.CategorySummaryDTO;
import com.finance.dashboard.dto.DashboardSummaryDTO;
import com.finance.dashboard.dto.MonthlySummaryDTO;
import com.finance.dashboard.repository.FinancialRecordRepository;
import com.finance.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final FinancialRecordRepository repository;

    @Override
    public DashboardSummaryDTO getSummary() {
        BigDecimal income = repository.getTotalIncome();
        BigDecimal expense = repository.getTotalExpense();
        return new DashboardSummaryDTO(income, expense, income.subtract(expense));
    }
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
    @Override
    public List<MonthlySummaryDTO> getMonthlySummary() {

        return repository.getMonthlyTotals()
                .stream()
                .map(obj -> new MonthlySummaryDTO(
                        (Integer) obj[0],
                        (Integer) obj[1],
                        (BigDecimal) obj[2]
                ))
                .toList();
    }
}