package com.cloud.communitiesservice.dto.member;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class MemberDto {
    private UUID id;
    private String userName;
    private String email;
}
