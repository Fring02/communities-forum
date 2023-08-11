package com.cloud.usersservice.dto.user;

import com.cloud.usersservice.entity.User;
import jakarta.persistence.ManyToMany;

import java.util.List;

public interface UserFullViewDto extends UserViewDto {
    String getFirstName();
    String getLastName();
    List<UserViewDto> getSubscribers();
    List<UserViewDto> getSubscriberOf();
}
