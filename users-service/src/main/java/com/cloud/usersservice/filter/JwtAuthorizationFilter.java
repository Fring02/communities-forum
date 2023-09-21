package com.cloud.usersservice.filter;

import com.cloud.usersservice.util.JwtUtilService;
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
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtUtilService jwtService;
    private final Logger logger = LoggerFactory.getLogger(JwtAuthorizationFilter.class);
    public JwtAuthorizationFilter(JwtUtilService jwtService) {
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
        logger.info("Authorizing request with JWT token " + token);
        if(SecurityContextHolder.getContext().getAuthentication() == null){
                var roles = jwtService.getRolesFromToken(token);
                var userDetails = jwtService.getUserDetailsFromToken(token, roles);
                var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                logger.info("Request is authenticated.");
                SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            logger.info("Request is already authenticated");
        }
        filterChain.doFilter(request, response);
    }
}
