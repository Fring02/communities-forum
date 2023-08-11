package com.cloud.authorizationservice.repository;

import com.cloud.authorizationservice.entity.LoginId;
import com.cloud.authorizationservice.entity.LoginInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginsRepository extends JpaRepository<LoginInfo, LoginId> {
    boolean existsByUsername(String username);
    Optional<LoginInfo> findByUsername(String username);
    boolean existsByRefreshToken(String refreshToken);
}
