package com.cloud.usersservice.service;

import com.cloud.usersservice.dto.subscribe.SubscriptionDto;
import com.cloud.usersservice.dto.user.*;
import com.cloud.usersservice.service.base.CrudService;
import jakarta.persistence.EntityNotFoundException;

import java.util.Optional;
import java.util.UUID;

public interface UsersService extends CrudService<UUID, UserViewDto, UserFullViewDto, UserCreateDto, UserCreatedDto, UserUpdateDto> {
    Optional<UserWithRolesDto> getRolesByUsername(String username);
    void subscribe(SubscriptionDto subscriptionDto) throws EntityNotFoundException, IllegalArgumentException;
}
