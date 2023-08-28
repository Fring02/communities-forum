package com.cloud.usersservice.dto.user;

import java.util.Collection;

public interface UserFullViewDto extends UserViewDto {
    String getFirstName();
    String getLastName();
    Collection<UserViewDto> getSubscribers();
    Collection<UserViewDto> getSubscriberOf();
}
