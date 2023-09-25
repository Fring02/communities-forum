package com.cloud.configserver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Order(1)
public class SecurityConfig {
    @Value("${spring.cloud.config.server.git.uri}")
    private String uri;
    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeHttpRequests().anyRequest().authenticated().and().httpBasic();
        System.out.println("URI: " + uri);
        return http.build();
    }
    @Bean
    public CommandLineRunner check(){
        return args -> {
            System.out.println("URI: " + uri);
        };
    }
}
