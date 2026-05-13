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
import java.util.logging.Logger;

@ApplicationScoped
@Transactional
public class CategoryService {

    private static final Logger log = Logger.getLogger(CategoryService.class.getName());

    @Inject
    private CategoryRepository categoryRepository;

    public List<CategoryResponse> getAll(String typeParam) {
        if (typeParam == null || typeParam.isBlank()) {
            log.fine("Fetching all categories");
            return categoryRepository.findAll().stream()
                    .map(this::toResponse)
                    .toList();
        }
        CategoryType type = parseType(typeParam);
        log.fine("Fetching categories by type: " + type);
        return categoryRepository.findAllByType(type).stream()
                .map(this::toResponse)
                .toList();
    }

    public CategoryResponse getById(Long id) {
        log.fine("Fetching category id=" + id);
        return toResponse(findOrThrow(id));
    }

    public CategoryResponse create(CategoryRequest request) {
        CategoryType type = parseType(request.type());
        if (categoryRepository.existsByName(request.name())) {
            log.warning("Duplicate category name: " + request.name());
            throw new BudgetException("Category with name '" + request.name() + "' already exists",
                    ErrorCode.CATEGORY_DUPLICATE, 409);
        }
        Category category = new Category();
        category.setName(request.name());
        category.setType(type);
        category.setColor(request.color());
        categoryRepository.save(category);
        log.info("Created category id=" + category.getId() + " name=" + category.getName());
        return toResponse(category);
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = findOrThrow(id);
        category.setName(request.name());
        category.setColor(request.color());
        categoryRepository.save(category);
        log.info("Updated category id=" + id);
        return toResponse(category);
    }

    public void delete(Long id) {
        categoryRepository.delete(findOrThrow(id));
        log.info("Deleted category id=" + id);
    }

    private Category findOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warning("Category not found: id=" + id);
                    return new BudgetException("Category not found with id " + id,
                            ErrorCode.CATEGORY_NOT_FOUND, 404);
                });
    }

    private CategoryType parseType(String type) {
        try {
            return CategoryType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warning("Invalid category type: " + type);
            throw new BudgetException("Invalid category type: " + type, ErrorCode.INVALID_TYPE, 400);
        }
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getType(), category.getColor());
    }
}
