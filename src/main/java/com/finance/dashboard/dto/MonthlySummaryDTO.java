package com.finance.dashboard.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class MonthlySummaryDTO {

    private Integer year;
    private Integer month;
    private BigDecimal income = BigDecimal.ZERO;
    private BigDecimal expense = BigDecimal.ZERO;

    public MonthlySummaryDTO(Integer year, Integer month) {
        this.year = year;
        this.month = month;
    }
}
