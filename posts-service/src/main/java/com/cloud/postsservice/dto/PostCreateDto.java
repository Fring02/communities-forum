package com.cloud.postsservice.dto;

import jakarta.validation.constraints.NotBlank;
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
}
