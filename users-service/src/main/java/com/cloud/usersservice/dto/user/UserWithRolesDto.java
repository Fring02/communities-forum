package com.cloud.usersservice.dto.user;

import com.cloud.usersservice.dto.base.DtoWithId;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
public interface UserWithRolesDto extends DtoWithId<UUID> {
    List<String> getRoles();
}
