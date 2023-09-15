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

import static org.springframework.security.config.Customizer.withDefaults;

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
                .pathMatchers("/posts/categories").hasAnyRole("admin", "moderator")
                .pathMatchers(HttpMethod.GET, "/posts/{id}/views").permitAll()
                .pathMatchers(HttpMethod.DELETE, "/posts/{id}").hasAnyRole("moderator", "user")
                .pathMatchers(HttpMethod.GET, "/posts/{id}").permitAll()
                .pathMatchers(HttpMethod.GET, "/posts").permitAll()
                .pathMatchers("/posts/**").authenticated()
                //Communities service endpoints
                .pathMatchers(HttpMethod.POST, "/communities/{id}/categories").hasRole("moderator")
                .pathMatchers(HttpMethod.POST, "/communities/{id}/members").hasAnyRole("admin", "moderator")
                .pathMatchers(HttpMethod.PATCH, "/communities/{id}").hasRole("admin")
                .pathMatchers(HttpMethod.DELETE, "/communities/{id}").hasRole("admin")
                .pathMatchers(HttpMethod.GET, "/communities/{id}").permitAll()
                .pathMatchers(HttpMethod.GET, "/communities").permitAll()
                .pathMatchers("/communities/**").authenticated()

                .and().csrf().disable().httpBasic().disable();
        http.addFilterAt(authFilter(), SecurityWebFiltersOrder.AUTHENTICATION);
        return http.build();
    }
    @Bean
    public AuthenticationFilter authFilter(){
        return new AuthenticationFilter(jwtUtilService);
    }
}