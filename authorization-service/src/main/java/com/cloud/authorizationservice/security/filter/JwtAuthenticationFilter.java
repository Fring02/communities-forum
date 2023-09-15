package com.cloud.authorizationservice.security.filter;

import com.cloud.authorizationservice.security.util.JwtUtilService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Objects;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtilService jwtService;
    private final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    public JwtAuthenticationFilter(JwtUtilService jwtService) {
        this.jwtService = Objects.requireNonNull(jwtService);
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(authHeader == null || authHeader.isBlank() || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }
        final String token = authHeader.split(" ")[1].trim();
        if(StringUtils.isBlank(token)){
            logger.warn("Authorization header doesn't contain token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        logger.info("Authenticating request with JWT token " + token);
        if(SecurityContextHolder.getContext().getAuthentication() == null){
            try {
                var userDetails = jwtService.validateTokenAndGetUser(token);
                if(userDetails == null) {
                    logger.warn("Couldn't fetch user from token, unauthorized");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
                var authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(),userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                logger.info("Request is authenticated. Setting authentication to true");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            catch (IllegalArgumentException | UsernameNotFoundException | JwtException e){
                logger.warn("Unauthorized: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
            logger.info("Request is authenticated");
        }
        filterChain.doFilter(request, response);
    }
}
