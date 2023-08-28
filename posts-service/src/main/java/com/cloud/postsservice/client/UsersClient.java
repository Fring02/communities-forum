package com.cloud.postsservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

@FeignClient("http://users-service")
public interface UsersClient {
    @PostMapping("/api/users/{id}/exists")
    ResponseEntity<Boolean> userExists(@PathVariable UUID id);
    @GetMapping("/api/users/{id}/karma")
    ResponseEntity<Integer> getUserRoles(@PathVariable UUID id);
}

