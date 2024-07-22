package com.cloud.usersservice.dto.user;

import com.cloud.usersservice.dto.base.DtoWithId;

import java.util.List;
import java.util.UUID;

public interface UserWithSubscribersDto extends DtoWithId<UUID> {
    List<UserWithSubscribersDto> getSubscribers();
    List<UserWithSubscribersDto> getSubscriberOf();
}
