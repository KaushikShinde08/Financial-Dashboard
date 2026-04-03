package com.finance.dashboard.dto;

import com.finance.dashboard.entity.Category;
import com.finance.dashboard.entity.RecordType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class FinancialRecordRequestDTO {

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private RecordType type;

    @NotNull
    private Category category;

    @NotNull
    private LocalDate date;

    private String notes;
}