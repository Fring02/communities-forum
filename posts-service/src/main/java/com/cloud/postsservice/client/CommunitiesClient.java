package com.cloud.postsservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.Set;

@FeignClient("http://communities-service")
public interface CommunitiesClient {
    @GetMapping("/api/v1/communities/{id}/exists")
    boolean communityExists(@PathVariable long id);
    @GetMapping("/api/v1/communities/{id}")
    long getById(@PathVariable long id, @RequestParam boolean karmaOnly);
    @GetMapping("/api/v1/communities/{id}/categories")
    Set<String> getCommunityCategories(@PathVariable long id);
    @GetMapping("/{id}/members/{username}/roles")
    Set<String> getCommunityRoles(@PathVariable long id, @PathVariable String username);
}
