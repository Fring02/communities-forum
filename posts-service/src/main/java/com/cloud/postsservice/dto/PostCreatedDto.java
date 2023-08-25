package com.cloud.postsservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PostCreatedDto extends PostCreateDto{
    private long id;
    private long viewCount;
    private LocalDate postedAt;
}
