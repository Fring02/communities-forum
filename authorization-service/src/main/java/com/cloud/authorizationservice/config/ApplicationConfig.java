package com.cloud.authorizationservice.config;

import com.cloud.authorizationservice.util.DatabaseSeeder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class ApplicationConfig {
    private final DatabaseSeeder seeder;
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
    @Bean
    public CommonsRequestLoggingFilter loggingFilter(){
        var filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setIncludeHeaders(true);
        filter.setAfterMessagePrefix("Request details: ");
        return filter;
    }
    public ApplicationConfig(DatabaseSeeder seeder) {
        this.seeder = seeder;
    }
    @Bean
    public CommandLineRunner commandLineRunner(){
        return args -> {
            seeder.seedUsers();
        };
    }
}
