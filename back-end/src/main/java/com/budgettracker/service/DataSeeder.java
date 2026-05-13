package com.budgettracker.service;

import com.budgettracker.model.Category;
import com.budgettracker.model.CategoryType;
import com.budgettracker.model.Transaction;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Stateless
public class DataSeeder {

    private static final Logger log = Logger.getLogger(DataSeeder.class.getName());

    @PersistenceContext(unitName = "BudgetTrackerPU")
    private EntityManager em;

    private record TxData(int daysAgo, String category, String description, double amount) {}

    public void resetData() {
        log.info("Wiping and re-seeding mock data...");

        em.createQuery("DELETE FROM Transaction t").executeUpdate();
        em.createQuery("DELETE FROM Category c").executeUpdate();
        em.flush();

        Map<String, Category> cats = seedCategories();
        seedTransactions(cats);

        log.info("Mock data seeded successfully.");
    }

    private Map<String, Category> seedCategories() {
        Category salary        = category("Salary",        CategoryType.INCOME,  "#2f9e44");
        Category freelance     = category("Freelance",     CategoryType.INCOME,  "#099268");
        Category rent          = category("Rent",          CategoryType.EXPENSE, "#e03131");
        Category groceries     = category("Groceries",     CategoryType.EXPENSE, "#f08c00");
        Category transport     = category("Transport",     CategoryType.EXPENSE, "#1971c2");
        Category utilities     = category("Utilities",     CategoryType.EXPENSE, "#7048e8");
        Category dining        = category("Dining Out",    CategoryType.EXPENSE, "#e64980");
        Category entertainment = category("Entertainment", CategoryType.EXPENSE, "#1098ad");
        Category healthcare    = category("Healthcare",    CategoryType.EXPENSE, "#ae3ec9");

        return Map.of(
            "Salary",        salary,
            "Freelance",     freelance,
            "Rent",          rent,
            "Groceries",     groceries,
            "Transport",     transport,
            "Utilities",     utilities,
            "Dining Out",    dining,
            "Entertainment", entertainment,
            "Healthcare",    healthcare
        );
    }

    private Category category(String name, CategoryType type, String color) {
        Category c = new Category();
        c.setName(name);
        c.setType(type);
        c.setColor(color);
        em.persist(c);
        return c;
    }

    private void seedTransactions(Map<String, Category> cats) {
        List<TxData> data = List.of(
            // Current month
            new TxData(2,  "Salary",        "Monthly salary",         3200.00),
            new TxData(2,  "Rent",          "Monthly rent",            950.00),
            new TxData(3,  "Groceries",     "Supermarket",              67.40),
            new TxData(5,  "Transport",     "Monthly bus pass",         49.00),
            new TxData(7,  "Utilities",     "Electricity and water",    94.50),
            new TxData(8,  "Dining Out",    "Dinner with friends",      42.00),
            new TxData(10, "Healthcare",    "Pharmacy",                 28.50),
            new TxData(11, "Groceries",     "Supermarket",              54.20),
            new TxData(13, "Entertainment", "Cinema",                   24.00),
            new TxData(14, "Dining Out",    "Lunch",                    18.50),
            new TxData(15, "Groceries",     "Supermarket",              71.30),
            new TxData(17, "Transport",     "Taxi",                     15.00),
            new TxData(19, "Freelance",     "Web project payment",     650.00),
            new TxData(20, "Dining Out",    "Restaurant dinner",        38.00),

            // Previous month
            new TxData(32, "Salary",        "Monthly salary",         3200.00),
            new TxData(32, "Rent",          "Monthly rent",            950.00),
            new TxData(34, "Groceries",     "Supermarket",              59.80),
            new TxData(35, "Transport",     "Monthly bus pass",         49.00),
            new TxData(36, "Utilities",     "Electricity and water",   102.30),
            new TxData(38, "Dining Out",    "Birthday dinner",          55.00),
            new TxData(40, "Groceries",     "Supermarket",              48.60),
            new TxData(42, "Entertainment", "Concert tickets",          60.00),
            new TxData(44, "Freelance",     "Design project",          480.00),
            new TxData(45, "Healthcare",    "Doctor visit",             50.00),
            new TxData(47, "Groceries",     "Supermarket",              63.10),
            new TxData(49, "Dining Out",    "Lunch",                    22.00),
            new TxData(51, "Transport",     "Train tickets",            32.50),
            new TxData(53, "Entertainment", "Streaming subscriptions",  14.99),
            new TxData(55, "Groceries",     "Supermarket",              77.40),

            // Two months ago
            new TxData(62, "Salary",        "Monthly salary",         3200.00),
            new TxData(62, "Rent",          "Monthly rent",            950.00),
            new TxData(63, "Groceries",     "Supermarket",              82.30),
            new TxData(64, "Transport",     "Monthly bus pass",         49.00),
            new TxData(65, "Utilities",     "Electricity and water",    88.70),
            new TxData(67, "Freelance",     "Consulting fee",          800.00),
            new TxData(68, "Dining Out",    "Team lunch",               35.00),
            new TxData(70, "Entertainment", "Museum tickets",           12.00),
            new TxData(72, "Healthcare",    "Dental checkup",           75.00),
            new TxData(73, "Groceries",     "Supermarket",              56.90),
            new TxData(75, "Dining Out",    "Restaurant",               45.00),
            new TxData(77, "Transport",     "Parking fees",             18.00),
            new TxData(79, "Groceries",     "Supermarket",              69.50),
            new TxData(81, "Entertainment", "Online course",            29.99),
            new TxData(83, "Freelance",     "Logo design",             350.00),
            new TxData(85, "Dining Out",    "Lunch",                    21.50),
            new TxData(87, "Groceries",     "Supermarket",              51.80),
            new TxData(89, "Transport",     "Taxi",                     12.50)
        );

        LocalDate today = LocalDate.now();
        for (TxData d : data) {
            Category cat = cats.get(d.category());
            Transaction tx = new Transaction();
            tx.setAmount(BigDecimal.valueOf(d.amount()));
            tx.setDescription(d.description());
            tx.setDate(today.minusDays(d.daysAgo()));
            tx.setType(cat.getType());
            tx.setCategory(cat);
            em.persist(tx);
        }
    }
}
