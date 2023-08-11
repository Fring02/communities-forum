package com.cloud.authorizationservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
public class UserRolesDto {
    private UUID id;
    private List<String> roles;
}
