package com.finance.dashboard.mapper;

import com.finance.dashboard.dto.FinancialRecordRequestDTO;
import com.finance.dashboard.dto.FinancialRecordResponseDTO;
import com.finance.dashboard.entity.FinancialRecord;
import org.springframework.stereotype.Component;

@Component
public class FinancialRecordMapper {

    public FinancialRecord toEntity(FinancialRecordRequestDTO dto) {
        FinancialRecord record = new FinancialRecord();
        record.setAmount(dto.getAmount());
        record.setType(dto.getType());
        record.setCategory(dto.getCategory());
        record.setDate(dto.getDate());
        record.setNotes(dto.getNotes());
        return record;
    }

    public FinancialRecordResponseDTO toResponseDTO(FinancialRecord record) {
        return new FinancialRecordResponseDTO(
                record.getId(),
                record.getAmount(),
                record.getType(),
                record.getCategory(),
                record.getDate(),
                record.getNotes(),
                record.getCreatedBy().getName(),
                record.getCreatedAt()
        );
    }

    public void updateEntityFromDto(FinancialRecordRequestDTO dto, FinancialRecord record) {
        record.setAmount(dto.getAmount());
        record.setType(dto.getType());
        record.setCategory(dto.getCategory());
        record.setDate(dto.getDate());
        record.setNotes(dto.getNotes());
    }
}