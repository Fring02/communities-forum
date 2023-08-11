package com.cloud.authorizationservice.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class User {
    private UUID id;
    private String userName;
    private String password;
    private List<String> roles;
    public User(UUID id, String userName, String password, List<String> roles) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.roles = roles;
    }
    private String refreshToken;
    private LocalDate refreshTokenExpiryTime;
}
