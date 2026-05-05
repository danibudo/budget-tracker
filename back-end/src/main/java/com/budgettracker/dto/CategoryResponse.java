package com.budgettracker.dto;

import com.budgettracker.model.CategoryType;

public record CategoryResponse(Long id, String name, CategoryType type, String color) {}