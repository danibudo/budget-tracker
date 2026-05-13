package com.budgettracker.service;

import com.budgettracker.dto.ExpensesByCategoryItem;
import com.budgettracker.dto.MonthlyItem;
import com.budgettracker.dto.SummaryResponse;
import com.budgettracker.model.CategoryType;
import com.budgettracker.repository.TransactionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
@Transactional
public class SummaryService {

    private static final Logger log = Logger.getLogger(SummaryService.class.getName());

    @Inject
    private TransactionRepository transactionRepository;

    public SummaryResponse getSummary() {
        log.fine("Computing summary");
        BigDecimal totalIncome = transactionRepository.sumByType(CategoryType.INCOME);
        BigDecimal totalExpenses = transactionRepository.sumByType(CategoryType.EXPENSE);
        BigDecimal balance = totalIncome.subtract(totalExpenses);
        return new SummaryResponse(
                balance.toPlainString(),
                totalIncome.toPlainString(),
                totalExpenses.toPlainString()
        );
    }

    public List<ExpensesByCategoryItem> getExpensesByCategory() {
        log.fine("Computing expenses by category");
        return transactionRepository.findExpensesByCategory().stream()
                .map(row -> new ExpensesByCategoryItem(
                        (String) row[0],
                        ((BigDecimal) row[1]).toPlainString()
                ))
                .toList();
    }

    public List<MonthlyItem> getMonthly() {
        log.fine("Computing monthly totals");
        return transactionRepository.findMonthlyTotals().stream()
                .map(row -> new MonthlyItem(
                        (String) row[0],
                        ((BigDecimal) row[1]).toPlainString(),
                        ((BigDecimal) row[2]).toPlainString()
                ))
                .toList();
    }
}