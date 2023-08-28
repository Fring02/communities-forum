package com.cloud.postsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;
import java.util.List;

@Getter
@AllArgsConstructor
public class PostsListDto {
    private final Collection<PostViewDto> posts;
    private final long count;
}
