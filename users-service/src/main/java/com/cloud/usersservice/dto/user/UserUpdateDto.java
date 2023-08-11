package com.cloud.usersservice.dto.user;

import com.cloud.usersservice.dto.base.DtoWithId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserUpdateDto implements DtoWithId<UUID> {
    @JsonIgnore
    private UUID id;
    private String email;
    private String userName;
    private String password;
}
