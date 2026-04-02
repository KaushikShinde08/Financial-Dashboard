package com.finance.dashboard.service.impl;

import com.finance.dashboard.dto.FinancialRecordRequestDTO;
import com.finance.dashboard.dto.FinancialRecordResponseDTO;
import com.finance.dashboard.entity.FinancialRecord;
import com.finance.dashboard.entity.RecordType;
import com.finance.dashboard.entity.Status;
import com.finance.dashboard.entity.User;
import com.finance.dashboard.exception.BadRequestException;
import com.finance.dashboard.exception.ResourceNotFoundException;
import com.finance.dashboard.mapper.FinancialRecordMapper;
import com.finance.dashboard.repository.FinancialRecordRepository;
import com.finance.dashboard.repository.UserRepository;
import com.finance.dashboard.service.FinancialRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinancialRecordServiceImpl implements FinancialRecordService {

    private final FinancialRecordRepository recordRepository;
    private final UserRepository userRepository;
    private final FinancialRecordMapper mapper;

    @Override
    public FinancialRecordResponseDTO createRecord(FinancialRecordRequestDTO dto, String creatorEmail) {
        User user = userRepository.findByEmail(creatorEmail)
                .filter(u -> u.getStatus() == Status.ACTIVE)
                .orElseThrow(() -> new BadRequestException("User not found or is inactive"));

        FinancialRecord record = mapper.toEntity(dto);
        record.setCreatedBy(user);

        return mapper.toResponseDTO(recordRepository.save(record));
    }

    @Override
    public List<FinancialRecordResponseDTO> getAllRecords() {
        return recordRepository.findAll()
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Override
    public void deleteRecord(Long id) {
        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Record not found with id: " + id));

        recordRepository.delete(record);
    }

    @Override
    public List<FinancialRecordResponseDTO> getByType(RecordType type) {
        return recordRepository.findByType(type)
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Override
    public FinancialRecordResponseDTO updateRecord(Long id, FinancialRecordRequestDTO dto) {

        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Record not found with id: " + id));

        mapper.updateEntityFromDto(dto, record);

        FinancialRecord updated = recordRepository.save(record);

        return mapper.toResponseDTO(updated);
    }

    @Override
    public List<FinancialRecordResponseDTO> filterRecords(
            RecordType type,
            String category,
            LocalDate startDate,
            LocalDate endDate) {

        validateDateRange(startDate, endDate);

        return recordRepository
                .filterRecords(type, category, startDate, endDate)
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
    }
    private void validateDateRange(LocalDate start, LocalDate end) {
        if (start != null && end != null && start.isAfter(end)) {
            throw new BadRequestException("Start date cannot be after end date");
        }
    }
}