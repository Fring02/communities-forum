package com.cloud.communitiesservice.dto.community;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CommunityCreateDto {
    @NotBlank(message = "Name should not be empty")
    private String name;
    @NotBlank(message = "Tag should not be empty")
    private String tag;
    @NotNull(message = "Owner id should not be empty")
    private UUID ownerId;
    @NotBlank(message = "Owner username should not be empty")
    private String ownerUsername;
    @NotBlank(message = "Description should not be empty")
    private String description;
    @Min(value = 2, message = "Minimal number of required karma for any community: 2")
    private long requiredKarma;
}
