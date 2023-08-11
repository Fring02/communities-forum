package com.cloud.authorizationservice.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class TokensBody {
    private String accessToken;
    private String refreshToken;
}
