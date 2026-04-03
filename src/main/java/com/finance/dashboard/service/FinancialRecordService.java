package com.finance.dashboard.service;


import com.finance.dashboard.dto.FinancialRecordRequestDTO;
import com.finance.dashboard.dto.FinancialRecordResponseDTO;
import com.finance.dashboard.entity.Category;
import com.finance.dashboard.entity.RecordType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface FinancialRecordService {

    FinancialRecordResponseDTO createRecord(FinancialRecordRequestDTO dto, String creatorEmail);

    Page<FinancialRecordResponseDTO> getAllRecords(Pageable pageable);

    void deleteRecord(Long id);

    Page<FinancialRecordResponseDTO> getByType(RecordType type, Pageable pageable);

    FinancialRecordResponseDTO updateRecord(Long id, FinancialRecordRequestDTO dto);

    Page<FinancialRecordResponseDTO> filterRecords(
            RecordType type,
            Category category,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );
}
