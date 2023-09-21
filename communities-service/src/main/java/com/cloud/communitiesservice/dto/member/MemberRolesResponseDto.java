package com.cloud.communitiesservice.dto.member;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.UUID;

@Getter
@Setter
public class MemberRolesResponseDto {
    private UUID id;
    private Collection<String> roles;
}