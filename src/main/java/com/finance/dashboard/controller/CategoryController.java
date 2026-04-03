package com.finance.dashboard.controller;

import com.finance.dashboard.dto.CategoryResponseDTO;
import com.finance.dashboard.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private static final Set<Category> INCOME_CATEGORIES = Set.of(
            Category.SALARY, Category.FREELANCE, Category.BUSINESS,
            Category.INVESTMENT, Category.BONUS, Category.OTHER
    );

    private static final Set<Category> EXPENSE_CATEGORIES = Set.of(
            Category.RENT, Category.FOOD, Category.UTILITIES, Category.TRANSPORT,
            Category.SHOPPING, Category.HEALTH, Category.ENTERTAINMENT, Category.OTHER
    );

    @GetMapping
    public ResponseEntity<CategoryResponseDTO> getCategories() {
        CategoryResponseDTO response = new CategoryResponseDTO();

        response.setIncome(getIncomeCategories());
        response.setExpense(getExpenseCategories());

        return ResponseEntity.ok(response);
    }

    private List<String> getIncomeCategories() {
        return Arrays.stream(Category.values())
                .filter(INCOME_CATEGORIES::contains)
                .map(Enum::name)
                .toList();
    }

    private List<String> getExpenseCategories() {
        return Arrays.stream(Category.values())
                .filter(EXPENSE_CATEGORIES::contains)
                .map(Enum::name)
                .toList();
    }
}
