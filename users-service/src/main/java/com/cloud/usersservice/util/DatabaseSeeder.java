package com.cloud.usersservice.util;

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
        var admin = new User(UUID.randomUUID(),
                "Sultan", "Khassenov", "hasenovsultanbek@gmail.com", "fring0213", 2,
                List.of("superadmin", "user"));
        var secondUser = new User(UUID.randomUUID(), "Saule", "Koldybaeva", "sullek75@gmail.com", "sullek75", 1,
                List.of("user"));
        var thirdUser = new User(UUID.randomUUID(), "Dastan", "Khassenov", "dasthas12@gmail.com", "Pillager01",
                1, List.of("user"));
        var fourthUser = new User(UUID.randomUUID(), "Nurakhmet", "Azhibek", "n.azhibek@mail.ru", "tokyoshymkent",
                1, List.of("user"));
        var fifthUser = new User(UUID.randomUUID(), "Anvar", "Tolebayev", "anv.tul@gmu.edu", "hasthebest",
                1, List.of("user"));
        logger.info("Seeding admin user and others...");
        usersRepository.saveAll(List.of(admin, secondUser, thirdUser, fourthUser, fifthUser));
    }
}
