package com.finance.dashboard.controller;


import com.finance.dashboard.dto.FinancialRecordRequestDTO;
import com.finance.dashboard.dto.FinancialRecordResponseDTO;
import com.finance.dashboard.entity.Category;
import com.finance.dashboard.entity.RecordType;
import com.finance.dashboard.service.FinancialRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
@Tag(name = "Financial Records", description = "Operations related to ledger entries and transaction management")
public class FinancialRecordController {

    private final FinancialRecordService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new financial record", description = "Endpoint for Admins to commit a new ledger entry. Categories are validated against predefined enums.")
    public ResponseEntity<FinancialRecordResponseDTO> create(
            @Valid @RequestBody FinancialRecordRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(service.createRecord(dto, userDetails.getUsername()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @Operation(summary = "Retrieve all records with pagination", description = "Returns a paginated list of all active ledger entries.")
    public ResponseEntity<Page<FinancialRecordResponseDTO>> getAll(
            @PageableDefault(size = 15, sort = "date", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(service.getAllRecords(pageable));
    }

    @GetMapping("/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @Operation(summary = "Filter records by type (INCOME/EXPENSE)", description = "Returns a paginated list of records filtered by transaction type.")
    public ResponseEntity<Page<FinancialRecordResponseDTO>> getByType(
            @PathVariable RecordType type,
            @PageableDefault(size = 15, sort = "date", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(service.getByType(type, pageable));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft-delete a record", description = "Marks a record as deleted in the database without permanent removal, preserving audit history.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteRecord(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing record", description = "Allows Admins to modify amounts, dates, or categories of existing ledger entries.")
    public ResponseEntity<FinancialRecordResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody FinancialRecordRequestDTO dto) {

        return ResponseEntity.ok(service.updateRecord(id, dto));
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @Operation(summary = "Advanced filtered search", description = "Comprehensive search endpoint with date range, category, and type filtering.")
    public ResponseEntity<Page<FinancialRecordResponseDTO>> filter(
            @RequestParam(required = false) RecordType type,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @PageableDefault(size = 15, sort = "date", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(
                service.filterRecords(type, category, startDate, endDate, pageable)
        );
    }
}
