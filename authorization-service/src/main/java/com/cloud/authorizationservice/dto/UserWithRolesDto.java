package com.cloud.authorizationservice.dto;

import java.util.List;
import java.util.UUID;

public record UserWithRolesDto(UUID id, String firstName, String lastName, String email,
                               String userName, int karma, String password, List<String> roles){

}
