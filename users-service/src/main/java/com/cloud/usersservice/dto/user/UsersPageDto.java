package com.cloud.usersservice.dto.user;

import lombok.Getter;

import java.util.Collection;
import java.util.List;
@Getter
public class UsersPageDto extends UsersListDto {
    private final int pagesCount;
    public UsersPageDto(Collection<UserViewDto> users, long count, int pagesCount) {
        super(users, count);
        this.pagesCount = pagesCount;
    }
}
