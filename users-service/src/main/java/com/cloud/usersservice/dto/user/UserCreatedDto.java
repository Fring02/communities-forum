package com.cloud.usersservice.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
@Getter
@Setter
public class UserCreatedDto extends UserCreateDto {
    private UUID id;
    private Collection<String> roles;
}
