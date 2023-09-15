package com.cloud.authorizationservice.util;

import com.cloud.authorizationservice.entity.LoginInfo;
import com.cloud.authorizationservice.repository.LoginsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public final class DatabaseSeeder {
    private final LoginsRepository loginsRepository;
    private final Logger logger;
    public DatabaseSeeder(LoginsRepository loginsRepository) {
        this.loginsRepository = Objects.requireNonNull(loginsRepository);
        logger = LoggerFactory.getLogger(DatabaseSeeder.class);
    }
    public void seedUsers(){
        var admin = new LoginInfo("fring0213", "casper2001");
        var secondUser = new LoginInfo("Pillager01", "casper2001");
        logger.info("Seeding admin user and others...");
        loginsRepository.saveAll(List.of(admin, secondUser));
    }
}
