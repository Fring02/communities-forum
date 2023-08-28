package com.cloud.communitiesservice.dto.member;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
public class NewMemberDto {
    private UUID userId;
    private String role;
}
