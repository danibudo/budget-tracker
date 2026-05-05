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

@ApplicationScoped
@Transactional
public class SummaryService {

    @Inject
    private TransactionRepository transactionRepository;

    public SummaryResponse getSummary() {
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
        return transactionRepository.findExpensesByCategory().stream()
                .map(row -> new ExpensesByCategoryItem(
                        (String) row[0],
                        ((BigDecimal) row[1]).toPlainString()
                ))
                .toList();
    }

    public List<MonthlyItem> getMonthly() {
        return transactionRepository.findMonthlyTotals().stream()
                .map(row -> new MonthlyItem(
                        (String) row[0],
                        ((BigDecimal) row[1]).toPlainString(),
                        ((BigDecimal) row[2]).toPlainString()
                ))
                .toList();
    }
}