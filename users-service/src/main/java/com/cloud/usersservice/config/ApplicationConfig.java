package com.cloud.usersservice.config;

import com.cloud.usersservice.service.util.DatabaseSeeder;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    private final DatabaseSeeder seeder;
    public ApplicationConfig(DatabaseSeeder seeder) {
        this.seeder = seeder;
    }
    @Bean
    public CommandLineRunner commandLineRunner(){
        return args -> {
            seeder.seedUsers();
        };
    }
    @Bean
    public ModelMapper mapper(){
        return new ModelMapper();
    }
}
