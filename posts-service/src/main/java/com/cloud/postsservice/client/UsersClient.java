package com.cloud.postsservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient("http://users-service")
public interface UsersClient {
    @GetMapping("/api/v1/users/{id}/exists")
    boolean userExists(@PathVariable UUID id);
    @GetMapping("/api/v1/users/{id}/karma")
    ResponseEntity<Integer> getUserKarma(@PathVariable UUID id);
}

