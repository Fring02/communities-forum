package com.cloud.authorizationservice.security.service;

import com.cloud.authorizationservice.client.UsersClient;
import com.cloud.authorizationservice.entity.User;
import com.cloud.authorizationservice.repository.LoginsRepository;
import com.cloud.authorizationservice.security.user.LoginDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final LoginsRepository repository;
    private final UsersClient usersClient;
    private final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    public UserDetailsServiceImpl(LoginsRepository repository, UsersClient usersClient) {
        this.repository = Objects.requireNonNull(repository);
        this.usersClient = usersClient;
    }
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(username == null || username.isBlank()) throw new IllegalArgumentException("Username is empty or blank");
        var loginInfoOpt = repository.findByUsername(username);
        if(loginInfoOpt.isEmpty()) throw new UsernameNotFoundException("Username " + username + " not found");
        var response = usersClient.getUserRoles(username);
        if(!response.getStatusCode().is2xxSuccessful()){
            logger.error("Users service resource fetch error: " + response.getStatusCode().value());
            return null;
        }
        var loginInfo = loginInfoOpt.get();
        var userRoles = response.getBody();
        return new LoginDetails(new User(Objects.requireNonNull(userRoles).getId(),loginInfo.getUsername(),
                loginInfo.getPassword(), userRoles.getRoles()));
    }
}
