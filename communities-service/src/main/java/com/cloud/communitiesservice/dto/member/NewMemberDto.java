package com.cloud.communitiesservice.dto.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
public class NewMemberDto {
    @NotNull(message = "User id should not be empty")
    private UUID userId;
    @NotBlank(message = "Username is empty")
    private String username;
    @NotBlank(message = "Role is empty")
    private String role;
}
