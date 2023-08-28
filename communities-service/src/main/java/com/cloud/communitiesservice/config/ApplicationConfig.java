package com.cloud.communitiesservice.config;

import com.cloud.communitiesservice.util.DatabaseSeeder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @Autowired
    private DatabaseSeeder seeder;
    @Bean
    public ModelMapper mapper(){
        return new ModelMapper();
    }
    @Bean
    public CommandLineRunner commandLineRunner(){
        return args -> seeder.seedRoles();
    }
}
