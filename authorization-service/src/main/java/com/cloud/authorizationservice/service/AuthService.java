package com.cloud.authorizationservice.service;

import com.cloud.authorizationservice.client.UsersClient;
import com.cloud.authorizationservice.dto.LoginDto;
import com.cloud.authorizationservice.dto.RegisterDto;
import com.cloud.authorizationservice.entity.LoginInfo;
import com.cloud.authorizationservice.entity.TokensBody;
import com.cloud.authorizationservice.entity.User;
import com.cloud.authorizationservice.exception.ResourceNotFoundException;
import com.cloud.authorizationservice.repository.LoginsRepository;
import com.cloud.authorizationservice.security.user.LoginDetails;
import com.cloud.authorizationservice.security.util.JwtUtilService;
import jakarta.persistence.EntityExistsException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;

@Service
@RefreshScope
public class AuthService {
    private final LoginsRepository repository;
    private final JwtUtilService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UsersClient usersClient;
    private final Logger logger = LoggerFactory.getLogger(AuthService.class);
    @Value("${jwt.refreshLifetime}")
    private int refreshTokenLifetimeDays;
    public AuthService(LoginsRepository repository, JwtUtilService jwtService,
                       PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, UsersClient usersClient) {
        this.repository = repository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.usersClient = usersClient;
    }
    @Transactional
    public TokensBody login(LoginDto login){
        Objects.requireNonNull(login);
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken)
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login.username(), login.password()));
        UserDetails userDetails = (UserDetails) authentication.getDetails();
        var newRefreshToken = jwtService.generateRefreshToken();

        var loginInfo = repository.findByUsername(login.username()).get();
        loginInfo.setRefreshToken(newRefreshToken);
        loginInfo.setRefreshTokenExpiryDate(LocalDate.now().plusDays(refreshTokenLifetimeDays));
        repository.save(loginInfo);

        final String newAccessToken = jwtService.generateAccessToken(userDetails);
        return new TokensBody(newAccessToken, newRefreshToken);
    }
    @Transactional
    public TokensBody register(RegisterDto registerDto) throws ResourceNotFoundException {
        Objects.requireNonNull(registerDto);
        if(repository.existsByUsername(registerDto.userName())) throw new EntityExistsException("User with such credentials already exist");

        var encodedPassword = passwordEncoder.encode(registerDto.password());
        var refreshToken = jwtService.generateRefreshToken();

        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setUsername(registerDto.userName());
        loginInfo.setPassword(encodedPassword);
        loginInfo.setRefreshToken(refreshToken);
        loginInfo.setRefreshTokenExpiryDate(LocalDate.now().plusDays(refreshTokenLifetimeDays));
        repository.save(loginInfo);
        var response = usersClient.createUser(registerDto);
        if(!response.getStatusCode().is2xxSuccessful()){
            throw new ResourceNotFoundException();
        }
        User user = new User();
        user.setId(Objects.requireNonNull(response.getBody()).id());
        user.setUserName(registerDto.userName());
        user.setPassword(loginInfo.getPassword());
        user.setRoles(Objects.requireNonNull(response.getBody()).roles());

        //After successful registration, authenticate newly created user
        var authentication = new UsernamePasswordAuthenticationToken(registerDto.userName(), registerDto.password());
        UserDetails userDetails = new LoginDetails(user);
        authentication.setDetails(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String accessToken = jwtService.generateAccessToken(userDetails);
        return new TokensBody(accessToken, refreshToken);
    }
    //@PreAuthorize("isAuthenticated()")
    public TokensBody refresh(TokensBody tokens){
        Objects.requireNonNull(tokens);
        String oldAccessToken = tokens.getAccessToken(), oldRefreshToken = tokens.getRefreshToken();
        if(StringUtils.isBlank(oldAccessToken) || StringUtils.isBlank(oldRefreshToken))
            throw new BadCredentialsException("Access or refresh token are blank");
        if(!repository.existsByRefreshToken(oldRefreshToken))
            throw new BadCredentialsException("Provided refresh token and user's token do not match");
        var userDetails = jwtService.validateTokenAndGetUser(oldAccessToken);
        var loginInfoOpt = repository.findByUsername(userDetails.getUsername());
        if(loginInfoOpt.isEmpty())
            throw new BadCredentialsException("Username not found in token");

        logger.info("Refreshing both tokens");
        var loginInfo = loginInfoOpt.get();
        var newRefreshToken = jwtService.generateRefreshToken();
        loginInfo.setRefreshToken(newRefreshToken);
        loginInfo.setRefreshTokenExpiryDate(LocalDate.now().plusDays(refreshTokenLifetimeDays));
        repository.save(loginInfo);
        return new TokensBody(jwtService.generateAccessToken(userDetails), newRefreshToken);
    }
    //@PreAuthorize("isAuthenticated()")
    public void revoke(TokensBody tokens){
        Objects.requireNonNull(tokens);
        String accessToken = tokens.getAccessToken(), refresh = tokens.getRefreshToken();
        if(StringUtils.isBlank(accessToken) || StringUtils.isBlank(refresh))
            throw new BadCredentialsException("Access or refresh token is blank");
        if(!repository.existsByRefreshToken(refresh))
            throw new BadCredentialsException("Provided refresh token and user's token do not match");
        var username = jwtService.getUsernameFromToken(accessToken);
        var loginInfoOpt = repository.findByUsername(username);
        if(loginInfoOpt.isEmpty()) throw new BadCredentialsException("Username not found from token");
        var user = loginInfoOpt.get();
        user.setRefreshToken(null);
        user.setRefreshTokenExpiryDate(null);
        logger.info("Revoking access and refresh tokens");
        repository.save(user);
    }
}
