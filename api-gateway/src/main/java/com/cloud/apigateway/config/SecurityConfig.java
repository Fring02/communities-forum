package com.cloud.apigateway.config;

import com.cloud.apigateway.filter.AuthenticationFilter;
import com.cloud.apigateway.util.JwtUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {
    @Autowired
    private JwtUtilService jwtUtilService;
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange()
                //Users service endpoints
                .pathMatchers(HttpMethod.GET,"/users").hasRole("superadmin")
                .pathMatchers(HttpMethod.DELETE,"/users/{id}").hasRole("superadmin")
                .pathMatchers("/users/**").authenticated()
                //Authorization service endpoints
                .pathMatchers(HttpMethod.PATCH, "/auth/refresh").authenticated()
                .pathMatchers(HttpMethod.DELETE, "/auth/revoke").authenticated()
                .pathMatchers("/auth/**").permitAll()
                //Posts service endpoints
                .pathMatchers(HttpMethod.GET, "/posts/{id}/views").permitAll()
                .pathMatchers(HttpMethod.DELETE, "/posts/{id}").authenticated()
                .pathMatchers(HttpMethod.GET, "/posts/{id}").permitAll()
                .pathMatchers(HttpMethod.GET, "/posts").permitAll()
                .pathMatchers("/posts/**").authenticated()
                //Communities service endpoints
                .pathMatchers(HttpMethod.POST, "/communities/{id}/categories").authenticated()
                .pathMatchers(HttpMethod.POST, "/communities/{id}/members").authenticated()
                .pathMatchers(HttpMethod.PATCH, "/communities/{id}").authenticated()
                .pathMatchers(HttpMethod.DELETE, "/communities/{id}").authenticated()
                .pathMatchers(HttpMethod.GET, "/communities/{id}").permitAll()
                .pathMatchers(HttpMethod.GET, "/communities").permitAll()
                .pathMatchers("/communities/**").authenticated()
                //Notifications service endpoints
                .pathMatchers("/notifications/**").authenticated()
                .and().csrf().disable().httpBasic().disable();
        http.addFilterAt(authFilter(), SecurityWebFiltersOrder.AUTHENTICATION);
        return http.build();
    }
    @Bean
    public AuthenticationFilter authFilter(){
        return new AuthenticationFilter(jwtUtilService);
    }
}