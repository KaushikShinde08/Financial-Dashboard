package com.finance.dashboard.service;

import com.finance.dashboard.dto.CategorySummaryDTO;
import com.finance.dashboard.dto.DashboardSummaryDTO;
import com.finance.dashboard.dto.MonthlySummaryDTO;

import java.util.List;

public interface DashboardService {

    DashboardSummaryDTO getSummary();

    List<CategorySummaryDTO> getCategorySummary();

    List<MonthlySummaryDTO> getMonthlySummary();
}