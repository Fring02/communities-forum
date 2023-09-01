package com.cloud.postsservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("http://communities-service")
public interface CommunitiesClient {
    @GetMapping("/api/v1/communities/{id}/exists")
    boolean communityExists(@PathVariable long id);
}
