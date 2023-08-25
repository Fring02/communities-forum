package com.cloud.postsservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostUpdateDto implements DtoWithId<Long> {
    private long id;
    private String title;
    private String description;
    int incrementViews = 0;
    int incrementLikes = 0;
    int incrementDislikes = 0;
    @Override
    public Long getId() {
        return id;
    }
}
