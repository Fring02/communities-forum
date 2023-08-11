package com.cloud.usersservice.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UsersListDto {
    private final List<UserViewDto> users;
    private final long count;
}
