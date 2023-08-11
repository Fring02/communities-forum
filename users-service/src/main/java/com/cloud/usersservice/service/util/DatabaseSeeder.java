package com.cloud.usersservice.service.util;

import com.cloud.usersservice.entity.User;
import com.cloud.usersservice.repository.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public final class DatabaseSeeder {
    private final UsersRepository usersRepository;
    private final Logger logger;
    public DatabaseSeeder(UsersRepository usersRepository) {
        this.usersRepository = Objects.requireNonNull(usersRepository);
        logger = LoggerFactory.getLogger(DatabaseSeeder.class);
    }
    public void seedUsers(){
        var admin = new User();
        admin.setId(UUID.randomUUID());
        admin.setEmail("hasenovsultanbek@gmail.com");
        admin.setFirstName("Sultan");
        admin.setLastName("Khassenov");
        admin.setUserName("fring0213");
        admin.setKarma(2);
        admin.setRoles(List.of("admin", "user"));
        var secondUser = new User();
        secondUser.setId(UUID.randomUUID());
        secondUser.setEmail("sullek75@gmail.com");
        secondUser.setFirstName("Saule");
        secondUser.setLastName("Koldybaeva");
        secondUser.setUserName("sullek75");
        secondUser.setKarma(1);
        secondUser.setRoles(List.of("user"));
        logger.info("Seeding admin user and second sample user...");
        usersRepository.saveAll(List.of(admin, secondUser));
    }
}
