package com.cloud.authorizationservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "login")
@Getter
@Setter
@IdClass(LoginId.class)
public class LoginInfo {
    @Id
    @Column(name = "username")
    private String username;
    @Id
    @Column(name = "password")
    private String password;
    @Column(name = "refreshToken")
    private String refreshToken;
    @Column(name = "refreshTokenExpiryDate")
    private LocalDate refreshTokenExpiryDate = LocalDate.now();
}
