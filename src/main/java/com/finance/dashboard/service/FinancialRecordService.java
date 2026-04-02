package com.finance.dashboard.service;


import com.finance.dashboard.dto.FinancialRecordRequestDTO;
import com.finance.dashboard.dto.FinancialRecordResponseDTO;
import com.finance.dashboard.entity.RecordType;

import java.time.LocalDate;
import java.util.List;

public interface FinancialRecordService {

    FinancialRecordResponseDTO createRecord(FinancialRecordRequestDTO dto, String creatorEmail);

    List<FinancialRecordResponseDTO> getAllRecords();

    void deleteRecord(Long id);

    List<FinancialRecordResponseDTO> getByType(RecordType type);

    FinancialRecordResponseDTO updateRecord(Long id, FinancialRecordRequestDTO dto);

    List<FinancialRecordResponseDTO> filterRecords(
            RecordType type,
            String category,
            LocalDate startDate,
            LocalDate endDate
    );
}
