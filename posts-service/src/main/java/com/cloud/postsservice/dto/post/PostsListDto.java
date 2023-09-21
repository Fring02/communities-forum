package com.cloud.postsservice.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
public class PostsListDto {
    private final Collection<PostViewDto> posts;
    private final long count;
}
