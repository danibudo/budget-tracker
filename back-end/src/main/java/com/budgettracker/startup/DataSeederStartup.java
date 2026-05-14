package com.budgettracker.startup;

import com.budgettracker.service.DataSeeder;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@ApplicationScoped
public class DataSeederStartup {

    private static final Logger log = Logger.getLogger(DataSeederStartup.class.getName());

    @Inject
    private DataSeeder seeder;

    private ScheduledExecutorService scheduler;

    public void onStart(@Observes @Initialized(ApplicationScoped.class) Object event) {
        log.info("Application started — triggering initial data seed.");
        seeder.resetData();

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::dailyReset, 24, 24, TimeUnit.HOURS);
    }

    private void dailyReset() {
        log.info("Daily reset triggered.");
        seeder.resetData();
    }

    @PreDestroy
    public void onStop() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }
}
