package com.budgettracker.repository;

import com.budgettracker.model.Category;
import com.budgettracker.model.CategoryType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CategoryRepository {

    @PersistenceContext(unitName = "BudgetTrackerPU")
    private EntityManager em;

    public List<Category> findAll() {
        return em.createQuery("SELECT c FROM Category c ORDER BY c.name", Category.class)
                .getResultList();
    }

    public List<Category> findAllByType(CategoryType type) {
        return em.createQuery("SELECT c FROM Category c WHERE c.type = :type ORDER BY c.name", Category.class)
                .setParameter("type", type)
                .getResultList();
    }

    public Optional<Category> findById(Long id) {
        return Optional.ofNullable(em.find(Category.class, id));
    }

    public boolean existsByName(String name) {
        Long count = em.createQuery("SELECT COUNT(c) FROM Category c WHERE c.name = :name", Long.class)
                .setParameter("name", name)
                .getSingleResult();
        return count > 0;
    }

    public void save(Category category) {
        if (category.getId() == null) {
            em.persist(category);
        } else {
            em.merge(category);
        }
    }

    public void delete(Category category) {
        em.remove(em.contains(category) ? category : em.merge(category));
    }
}