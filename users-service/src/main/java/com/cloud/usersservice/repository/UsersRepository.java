package com.cloud.usersservice.repository;

import com.cloud.usersservice.dto.user.UserViewDto;
import com.cloud.usersservice.dto.user.UserWithRolesDto;
import com.cloud.usersservice.entity.User;
import com.cloud.usersservice.repository.base.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsersRepository extends BaseRepository<User, UUID> {
    boolean existsByEmailOrUserNameAllIgnoreCase(String email, String userName);
    Optional<UserWithRolesDto> findByUserName(String username);
    Collection<UserViewDto> findByIdIsIn(Collection<UUID> ids);
}
