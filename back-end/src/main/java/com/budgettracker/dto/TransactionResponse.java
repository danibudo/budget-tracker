package com.budgettracker.dto;

import com.budgettracker.model.CategoryType;

public record TransactionResponse(
        Long id,
        String amount,
        String description,
        String date,
        CategoryType type,
        Long categoryId,
        String categoryName,
        String categoryColor
) {}