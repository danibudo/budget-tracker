package com.budgettracker.service;

import com.budgettracker.dto.TransactionRequest;
import com.budgettracker.dto.TransactionResponse;
import com.budgettracker.exception.BudgetException;
import com.budgettracker.exception.ErrorCode;
import com.budgettracker.model.Category;
import com.budgettracker.model.Transaction;
import com.budgettracker.repository.CategoryRepository;
import com.budgettracker.repository.TransactionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
@Transactional
public class TransactionService {

    private static final Logger log = Logger.getLogger(TransactionService.class.getName());

    @Inject
    private TransactionRepository transactionRepository;

    @Inject
    private CategoryRepository categoryRepository;

    public List<TransactionResponse> getAll(String fromParam, String toParam) {
        log.fine("Fetching transactions from=" + fromParam + " to=" + toParam);
        LocalDate from = fromParam != null ? parseDate(fromParam) : null;
        LocalDate to = toParam != null ? parseDate(toParam) : null;

        if (from != null && to != null && from.isAfter(to)) {
            log.warning("Invalid date range: from=" + from + " to=" + to);
            throw new BudgetException("'from' date must not be after 'to' date",
                    ErrorCode.INVALID_DATE_RANGE, 400);
        }

        List<Transaction> transactions;
        if (from == null && to == null) {
            transactions = transactionRepository.findAll();
        } else {
            transactions = transactionRepository.findAllByDateRange(
                    from != null ? from : LocalDate.MIN,
                    to != null ? to : LocalDate.MAX
            );
        }
        return transactions.stream().map(this::toResponse).toList();
    }

    public TransactionResponse getById(Long id) {
        log.fine("Fetching transaction id=" + id);
        return toResponse(findOrThrow(id));
    }

    public TransactionResponse create(TransactionRequest request) {
        BigDecimal amount = parseAmount(request.amount());
        Category category = findCategoryOrThrow(request.categoryId());

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDescription(request.description());
        transaction.setDate(parseDate(request.date()));
        transaction.setType(category.getType());
        transaction.setCategory(category);
        transactionRepository.save(transaction);
        log.info("Created transaction id=" + transaction.getId() + " amount=" + amount + " categoryId=" + category.getId());
        return toResponse(transaction);
    }

    public TransactionResponse update(Long id, TransactionRequest request) {
        Transaction transaction = findOrThrow(id);
        BigDecimal amount = parseAmount(request.amount());
        Category category = findCategoryOrThrow(request.categoryId());

        transaction.setAmount(amount);
        transaction.setDescription(request.description());
        transaction.setDate(parseDate(request.date()));
        transaction.setCategory(category);
        transaction.setType(category.getType());
        transactionRepository.save(transaction);
        log.info("Updated transaction id=" + id);
        return toResponse(transaction);
    }

    public void delete(Long id) {
        transactionRepository.delete(findOrThrow(id));
        log.info("Deleted transaction id=" + id);
    }

    private Transaction findOrThrow(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> {
                    log.warning("Transaction not found: id=" + id);
                    return new BudgetException("Transaction not found with id " + id,
                            ErrorCode.TRANSACTION_NOT_FOUND, 404);
                });
    }

    private Category findCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.warning("Category not found for transaction: categoryId=" + categoryId);
                    return new BudgetException("Category not found with id " + categoryId,
                            ErrorCode.CATEGORY_NOT_FOUND, 400);
                });
    }

    private LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            log.warning("Invalid date format: " + dateStr);
            throw new BudgetException("Invalid date format: " + dateStr + ". Expected YYYY-MM-DD",
                    ErrorCode.INVALID_DATE_RANGE, 400);
        }
    }

    private BigDecimal parseAmount(String amountStr) {
        try {
            BigDecimal amount = new BigDecimal(amountStr);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                log.warning("Non-positive amount: " + amountStr);
                throw new BudgetException("Amount must be positive", ErrorCode.INVALID_AMOUNT, 400);
            }
            return amount;
        } catch (NumberFormatException e) {
            log.warning("Invalid amount format: " + amountStr);
            throw new BudgetException("Invalid amount: " + amountStr, ErrorCode.INVALID_AMOUNT, 400);
        }
    }

    private TransactionResponse toResponse(Transaction t) {
        return new TransactionResponse(
                t.getId(),
                t.getAmount().toPlainString(),
                t.getDescription(),
                t.getDate().toString(),
                t.getType(),
                t.getCategory().getId(),
                t.getCategory().getName(),
                t.getCategory().getColor()
        );
    }
}
