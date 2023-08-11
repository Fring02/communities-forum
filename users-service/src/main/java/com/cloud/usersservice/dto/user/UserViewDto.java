package com.cloud.usersservice.dto.user;

import com.cloud.usersservice.dto.base.DtoWithId;

import java.util.UUID;

public interface UserViewDto extends DtoWithId<UUID> {
    String getEmail();
    String getUserName();
    int getKarma();
}
