package com.cloud.authorizationservice.config;

import com.cloud.authorizationservice.security.filter.JwtAuthenticationFilter;
import com.cloud.authorizationservice.security.service.AuthenticationProviderImpl;
import com.cloud.authorizationservice.security.util.JwtUtilService;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtUtilService jwtUtilService;
    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(new AuthenticationProviderImpl(userDetailsService, bCryptPasswordEncoder()));
        return authenticationManagerBuilder.build();
    }
    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeHttpRequests()
                .requestMatchers(HttpMethod.PUT, "/api/v1/auth/refresh").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/v1/auth/revoke").authenticated()
                .anyRequest().permitAll();
                /*.and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                .exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
                .and().oauth2ResourceServer().jwt()
                .and().and().oauth2Login();*/
        // Add JWT token filter
        http.addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean
    public Filter jwtAuthFilter(){
        return new JwtAuthenticationFilter(jwtUtilService);
    }
    @Value("${spring.websecurity.debug:false}")
    boolean webSecurityDebug;
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.debug(webSecurityDebug);
    }
}

