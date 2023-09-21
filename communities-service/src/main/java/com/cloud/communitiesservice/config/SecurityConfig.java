package com.cloud.communitiesservice.config;

import com.cloud.communitiesservice.filter.JwtAuthorizationFilter;
import com.cloud.communitiesservice.repository.CommunitiesMembersRepository;
import com.cloud.communitiesservice.repository.CommunityRolesRepository;
import com.cloud.communitiesservice.util.JwtUtilService;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http, JwtUtilService jwtUtilService) throws Exception {
        http.cors().and().csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeHttpRequests().anyRequest().permitAll();
        // Add JWT token filter
        http.addFilterBefore(jwtAuthFilter(jwtUtilService), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean
    public Filter jwtAuthFilter(JwtUtilService jwtUtilService){
        return new JwtAuthorizationFilter(jwtUtilService);
    }
    @Value("${spring.websecurity.debug:false}")
    boolean webSecurityDebug;
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.debug(webSecurityDebug);
    }
}

