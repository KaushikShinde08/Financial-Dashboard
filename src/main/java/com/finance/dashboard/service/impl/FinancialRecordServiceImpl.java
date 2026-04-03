package com.finance.dashboard.service.impl;

import com.finance.dashboard.dto.FinancialRecordRequestDTO;
import com.finance.dashboard.dto.FinancialRecordResponseDTO;
import com.finance.dashboard.entity.Category;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FinancialRecordServiceImpl implements FinancialRecordService {

    private static final Set<Category> INCOME_CATEGORIES = Set.of(
            Category.SALARY, Category.FREELANCE, Category.BUSINESS,
            Category.INVESTMENT, Category.BONUS, Category.OTHER
    );

    private static final Set<Category> EXPENSE_CATEGORIES = Set.of(
            Category.RENT, Category.FOOD, Category.UTILITIES, Category.TRANSPORT,
            Category.SHOPPING, Category.HEALTH, Category.ENTERTAINMENT, Category.OTHER
    );

    private final FinancialRecordRepository recordRepository;
    private final UserRepository userRepository;
    private final FinancialRecordMapper mapper;

    @CacheEvict(value = {"dashboardSummary", "categorySummary", "monthlySummary"}, allEntries = true)
    @Override
    public FinancialRecordResponseDTO createRecord(FinancialRecordRequestDTO dto, String creatorEmail) {
        User user = userRepository.findByEmail(creatorEmail)
                .filter(u -> u.getStatus() == Status.ACTIVE)
                .orElseThrow(() -> new BadRequestException("User not found or is inactive"));

        processCategory(dto);
        FinancialRecord record = mapper.toEntity(dto);
        record.setCreatedBy(user);

        return mapper.toResponseDTO(recordRepository.save(record));
    }

    @Override
    public Page<FinancialRecordResponseDTO> getAllRecords(Pageable pageable) {
        return recordRepository.findAll(pageable)
                .map(mapper::toResponseDTO);
    }

    @CacheEvict(value = {"dashboardSummary", "categorySummary", "monthlySummary"}, allEntries = true)
    @Override
    public void deleteRecord(Long id) {
        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Record not found with id: " + id));

        recordRepository.delete(record);
    }

    @Override
    public Page<FinancialRecordResponseDTO> getByType(RecordType type, Pageable pageable) {
        return recordRepository.findByType(type, pageable)
                .map(mapper::toResponseDTO);
    }

    @CacheEvict(value = {"dashboardSummary", "categorySummary", "monthlySummary"}, allEntries = true)
    @Override
    public FinancialRecordResponseDTO updateRecord(Long id, FinancialRecordRequestDTO dto) {
        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Record not found with id: " + id));


        validateCategory(dto.getType(), dto.getCategory());
        

        mapper.updateEntityFromDto(dto, record);

        FinancialRecord updated = recordRepository.save(record);

        return mapper.toResponseDTO(updated);
    }

    @Override
    public Page<FinancialRecordResponseDTO> filterRecords(
            RecordType type,
            Category category,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {

        validateDateRange(startDate, endDate);

        return recordRepository
                .filterRecords(type, category, startDate, endDate, pageable)
                .map(mapper::toResponseDTO);
    }

    private void validateDateRange(LocalDate start, LocalDate end) {
        if (bothDatesProvided(start, end) && start.isAfter(end)) {
            throw new BadRequestException("Start date cannot be after end date");
        }
    }

    private boolean bothDatesProvided(LocalDate start, LocalDate end) {
        return start != null && end != null;
    }

    private void processCategory(FinancialRecordRequestDTO dto) {
        validateCategory(dto.getType(), dto.getCategory());
    }

    private void validateCategory(RecordType type, Category category) {
        Set<Category> validCategories = getValidCategoriesForType(type);
        if (!validCategories.contains(category)) {
            throw new BadRequestException("Invalid category for " + type.name().toLowerCase() + " record: " + category);
        }
    }

    private Set<Category> getValidCategoriesForType(RecordType type) {
        return type == RecordType.INCOME ? INCOME_CATEGORIES : EXPENSE_CATEGORIES;
    }
}