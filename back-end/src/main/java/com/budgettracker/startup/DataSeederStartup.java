package com.budgettracker.startup;

import com.budgettracker.service.DataSeeder;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;

import java.util.logging.Logger;

@Singleton
@Startup
public class DataSeederStartup {

    private static final Logger log = Logger.getLogger(DataSeederStartup.class.getName());

    @Inject
    private DataSeeder seeder;

    @PostConstruct
    public void onStartup() {
        log.info("Application started — triggering initial data seed.");
        seeder.resetData();
    }

    // Runs every day at 03:00 server time
    @Schedule(hour = "3", minute = "0", second = "0", persistent = false)
    public void dailyReset() {
        log.info("Daily reset triggered.");
        seeder.resetData();
    }
}
