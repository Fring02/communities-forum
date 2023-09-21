package com.cloud.postsservice.dto.post;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PostCreateDto {
    @NotBlank(message = "Title must not be empty")
    private String title;
    @NotNull(message = "Owner id is empty")
    private UUID ownerId;
    @NotBlank(message = "Owner username is empty")
    private String ownerUsername;
    @NotBlank(message = "Description must not empty")
    private String description;
    @NotBlank(message = "Category should be chosen")
    private String category;
    @Min(value = 1, message = "Community id must be greater than 0")
    private long communityId;
}
