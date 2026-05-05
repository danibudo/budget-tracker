package com.budgettracker.service;

import com.budgettracker.dto.CategoryRequest;
import com.budgettracker.dto.CategoryResponse;
import com.budgettracker.exception.BudgetException;
import com.budgettracker.exception.ErrorCode;
import com.budgettracker.model.Category;
import com.budgettracker.model.CategoryType;
import com.budgettracker.repository.CategoryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
@Transactional
public class CategoryService {

    @Inject
    private CategoryRepository categoryRepository;

    public List<CategoryResponse> getAll(String typeParam) {
        if (typeParam == null || typeParam.isBlank()) {
            return categoryRepository.findAll().stream()
                    .map(this::toResponse)
                    .toList();
        }
        CategoryType type = parseType(typeParam);
        return categoryRepository.findAllByType(type).stream()
                .map(this::toResponse)
                .toList();
    }

    public CategoryResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public CategoryResponse create(CategoryRequest request) {
        CategoryType type = parseType(request.type());
        if (categoryRepository.existsByName(request.name())) {
            throw new BudgetException("Category with name '" + request.name() + "' already exists",
                    ErrorCode.CATEGORY_DUPLICATE, 409);
        }
        Category category = new Category();
        category.setName(request.name());
        category.setType(type);
        category.setColor(request.color());
        categoryRepository.save(category);
        return toResponse(category);
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = findOrThrow(id);
        category.setName(request.name());
        category.setColor(request.color());
        categoryRepository.save(category);
        return toResponse(category);
    }

    public void delete(Long id) {
        categoryRepository.delete(findOrThrow(id));
    }

    private Category findOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new BudgetException("Category not found with id " + id,
                        ErrorCode.CATEGORY_NOT_FOUND, 404));
    }

    private CategoryType parseType(String type) {
        try {
            return CategoryType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BudgetException("Invalid category type: " + type, ErrorCode.INVALID_TYPE, 400);
        }
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getType(), category.getColor());
    }
}
