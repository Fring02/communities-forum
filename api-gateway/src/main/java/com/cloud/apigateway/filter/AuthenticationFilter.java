package com.cloud.apigateway.filter;

import com.cloud.apigateway.util.JwtUtilService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RefreshScope
@Component
public class AuthenticationFilter implements WebFilter {
    private final JwtUtilService jwtUtil;
    private final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    public AuthenticationFilter(JwtUtilService jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    private Mono<Void> onUnauthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }
    private String getAuthHeader(ServerHttpRequest request) {
        return request.getHeaders().getOrEmpty("Authorization").get(0);
    }
    private void updateRequest(ServerWebExchange exchange, String token) {
        Claims claims = jwtUtil.getAllClaimsFromToken(token);
        exchange.getRequest().mutate()
                .header("email", String.valueOf(claims.get("email")))
                .build();
    }
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
            logger.info("Requesting resource...");
            if (!request.getHeaders().containsKey("Authorization")) {
                logger.warn("Headers don't contain 'Authorization' header");
                return chain.filter(exchange);
            }
            final var authHeader = getAuthHeader(request).split(" ");
            if(authHeader.length != 2 || !Objects.equals(authHeader[0], "Bearer")){
                logger.warn("Authorization header is invalid");
                return onUnauthorized(exchange);
            }
            final String token = authHeader[1];
            try {
                if (!jwtUtil.isValidAndNonExpired(token)) {
                    logger.warn("JWT token is invalid");
                    return onUnauthorized(exchange);
                }
                logger.info("Successful token verification...");
                updateRequest(exchange, token);
            } catch (JwtException e){
                logger.warn("JWT token is unreadable or expired");
                return onUnauthorized(exchange);
            }
        var roles = jwtUtil.getRolesFromToken(token); var userDetails = jwtUtil.getUserDetailsFromToken(token, roles);
        var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(userDetails);
        logger.info("Authentication context established.");
        return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
    }
}
