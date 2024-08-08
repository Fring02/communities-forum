package com.cloud.authorizationservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "login")
@Getter
@Setter
@IdClass(LoginId.class)
@NoArgsConstructor
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
    public LoginInfo(String username, String password){
        this.username = username;
        this.password = password;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LoginInfo loginInfo)) return false;
        return Objects.equals(getUsername(), loginInfo.getUsername()) && Objects.equals(getPassword(), loginInfo.getPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getPassword());
    }
}
