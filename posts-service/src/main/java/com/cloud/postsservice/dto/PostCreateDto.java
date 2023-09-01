package com.cloud.postsservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCreateDto {
    @NotBlank(message = "Title must not be empty")
    private String title;
    @NotBlank(message = "Owner id is empty")
    private String ownerId;
    @NotBlank(message = "Description must not empty")
    private String description;
    @NotBlank(message = "Category should be chosen")
    private String category;
    @Min(value = 1, message = "Community id must be greater than 0")
    private long communityId;
}
