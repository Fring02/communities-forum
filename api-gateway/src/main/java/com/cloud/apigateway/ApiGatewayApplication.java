package com.cloud.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.addOriginalRequestUrl;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

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
                                    chain.filter(appendApiPrefixFilter(exchange, "auth")))).uri("lb://authorization-service")).build();
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
