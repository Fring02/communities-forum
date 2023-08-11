package com.cloud.usersservice.dto.subscribe;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscriptionDto {
    @NotBlank(message = "User id is invalid")
    private String userId;
    @NotBlank(message = "Subscriber id is invalid")
    private String subscriberId;
}
