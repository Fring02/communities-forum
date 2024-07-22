package com.cloud.usersservice.dto.user;

import com.cloud.usersservice.dto.base.DtoWithId;

import java.util.UUID;

public interface UserKarmaDto extends DtoWithId<UUID> {
    int getKarma();
}
