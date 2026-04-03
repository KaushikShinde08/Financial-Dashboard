package com.finance.dashboard.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "financial_records")
@Getter
@Setter
@SQLDelete(sql = "UPDATE financial_records SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class FinancialRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Record type is required")
    @Enumerated(EnumType.STRING)
    private RecordType type;

    @NotNull(message = "Category is required")
    @Enumerated(EnumType.STRING)
    private Category category;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @Size(max = 255, message = "Notes must be less than 255 characters")
    private String notes;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private boolean deleted = false;
}
