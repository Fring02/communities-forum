package com.cloud.communitiesservice.client;

import com.cloud.communitiesservice.dto.CategoryRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("http://posts-service")
public interface PostsClient {
    @PostMapping("/api/v1/posts/categories")
    ResponseEntity<String> createPostCategoriesPerCommunity(@RequestBody CategoryRequestDto dto);
}
