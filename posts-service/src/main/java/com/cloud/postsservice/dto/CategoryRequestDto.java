package com.cloud.postsservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequestDto {
    private long communityId;
    private String category;
}
