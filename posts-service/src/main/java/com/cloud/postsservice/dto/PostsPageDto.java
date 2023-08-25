package com.cloud.postsservice.dto;

import java.util.List;

public class PostsPageDto extends PostsListDto {
    private final int pagesCount;
    public PostsPageDto(List<PostViewDto> users, long count, int pagesCount) {
        super(users, count);
        this.pagesCount = pagesCount;
    }
}
