package com.cloud.apigateway.config;

import com.cloud.apigateway.filter.AuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.addOriginalRequestUrl;

@Configuration
public class GatewayConfig {
    /*private final AuthenticationFilter authFilter;
    public GatewayConfig(AuthenticationFilter authFilter) {
        this.authFilter = authFilter;
    }*/
    @Bean
    public RouteLocator dynamicZipCodeRoute(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("users-service", r ->
                        r.path("/users/**").filters(f -> f.filter((exchange, chain) ->
                                        chain.filter(appendApiPrefixFilter(exchange, "users")))).uri("lb://users-service"))
                .route("posts-service", r ->
                        r.path("/posts/**").filters(f -> f.filter((exchange, chain) ->
                                chain.filter(appendApiPrefixFilter(exchange, "posts")))).uri("lb://posts-service"))
                .route("communities-service", r ->
                        r.path("/communities/**").filters(f -> f.filter((exchange, chain) ->
                                chain.filter(appendApiPrefixFilter(exchange, "communities")))).uri("lb://communities-service"))
                .route("authorization-service", r ->
                        r.path("/auth/**").filters(f -> f.filter((exchange, chain) ->
                                chain.filter(appendApiPrefixFilter(exchange, "auth")))/*.filter(authFilter)*/).uri("lb://authorization-service"))
                .build();
    }
    private ServerWebExchange appendApiPrefixFilter(ServerWebExchange exchange, String endpointPartName){
        ServerHttpRequest req = exchange.getRequest();
        addOriginalRequestUrl(exchange, req.getURI());
        String path = req.getURI().getRawPath();
        String newPath = path.replaceAll(
                "/" + endpointPartName + "(?<segment>/?.*)",
                "/api/v1/" + endpointPartName + "${segment}");
        ServerHttpRequest request = req.mutate().path(newPath).build();
        exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, request.getURI());
        return exchange.mutate().request(request).build();
    }
}
