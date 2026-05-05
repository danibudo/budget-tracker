package com.budgettracker.repository;

import com.budgettracker.model.CategoryType;
import com.budgettracker.model.Transaction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TransactionRepository {

    @PersistenceContext(unitName = "BudgetTrackerPU")
    private EntityManager em;

    public List<Transaction> findAll() {
        return em.createQuery(
                "SELECT t FROM Transaction t JOIN FETCH t.category ORDER BY t.date DESC", Transaction.class)
                .getResultList();
    }

    public List<Transaction> findAllByDateRange(LocalDate from, LocalDate to) {
        return em.createQuery(
                "SELECT t FROM Transaction t JOIN FETCH t.category WHERE t.date >= :from AND t.date <= :to ORDER BY t.date DESC",
                Transaction.class)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }

    public Optional<Transaction> findById(Long id) {
        return em.createQuery(
                "SELECT t FROM Transaction t JOIN FETCH t.category WHERE t.id = :id", Transaction.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }

    public void save(Transaction transaction) {
        if (transaction.getId() == null) {
            em.persist(transaction);
        } else {
            em.merge(transaction);
        }
    }

    public void delete(Transaction transaction) {
        em.remove(em.contains(transaction) ? transaction : em.merge(transaction));
    }

    public BigDecimal sumByType(CategoryType type) {
        BigDecimal result = em.createQuery(
                "SELECT SUM(t.amount) FROM Transaction t WHERE t.type = :type", BigDecimal.class)
                .setParameter("type", type)
                .getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> findExpensesByCategory() {
        return em.createQuery(
                "SELECT t.category.name, SUM(t.amount) FROM Transaction t WHERE t.type = :type GROUP BY t.category.name")
                .setParameter("type", CategoryType.EXPENSE)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> findMonthlyTotals() {
        return em.createNativeQuery(
                "SELECT DATE_FORMAT(t.date, '%Y-%m') AS month, " +
                "SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END) AS income, " +
                "SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END) AS expenses " +
                "FROM transactions t " +
                "GROUP BY DATE_FORMAT(t.date, '%Y-%m') " +
                "ORDER BY month ASC")
                .getResultList();
    }
}