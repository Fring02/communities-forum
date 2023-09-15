package com.cloud.authorizationservice.controller;

import com.cloud.authorizationservice.dto.LoginDto;
import com.cloud.authorizationservice.dto.RegisterDto;
import com.cloud.authorizationservice.entity.TokensBody;
import com.cloud.authorizationservice.exception.ResourceNotFoundException;
import com.cloud.authorizationservice.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RefreshScope
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    @Value("${jwt.expirationDuration}")
    private int tokenLifetime;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("/login")
    public ResponseEntity<TokensBody> login(@RequestBody @Valid LoginDto loginDto, @Autowired HttpServletResponse response){
        var tokens = authService.login(loginDto);
        appendToCookie(tokens, response);
        return ResponseEntity.ok(tokens);
    }
    @PostMapping("/register")
    public ResponseEntity<TokensBody> register(@RequestBody @Valid RegisterDto registerDto, @Autowired HttpServletResponse response) throws ResourceNotFoundException {
        var tokens = authService.register(registerDto);
        appendToCookie(tokens, response);
        return ResponseEntity.ok(tokens);
    }
    @PutMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader(name = "Access") String accessToken, @RequestHeader(name = "Refresh")
                                          String refreshToken, @Autowired HttpServletResponse response){
        var newTokens = authService.refresh(new TokensBody(accessToken, refreshToken));
        appendToCookie(newTokens, response);
        return ResponseEntity.ok(newTokens);
    }
    @DeleteMapping("/revoke")
    public ResponseEntity<?> revokeToken(@RequestHeader(name = "Access") String accessToken, @RequestHeader(name = "Refresh")
    String refreshToken, @Autowired HttpServletResponse response){
        authService.revoke(new TokensBody(accessToken, refreshToken));
        Cookie revokeAccessCookie = new Cookie("access", null);
        Cookie revokeRefreshCookie = new Cookie("refresh", null);
        revokeAccessCookie.setMaxAge(0);
        revokeRefreshCookie.setMaxAge(0);
        response.addCookie(revokeAccessCookie);
        response.addCookie(revokeRefreshCookie);
        return ResponseEntity.ok().build();
    }

    private void appendToCookie(TokensBody tokens, HttpServletResponse response){
        final var accessCookie = new Cookie("access", tokens.getAccessToken());
        final var refreshCookie = new Cookie("refresh", tokens.getRefreshToken());

        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setMaxAge(LocalDateTime.now().plusMinutes(tokenLifetime).getMinute());

        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setMaxAge(LocalDateTime.now().plusMinutes(tokenLifetime).getMinute());

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
    }
}
