package com.cloud.authorizationservice.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record LoginDto(@NotBlank(message = "Username is empty")
                       @Length(min = 5, max = 20, message = "Username's length must be from 5 till 20 characters") String username,
                       @NotBlank(message = "Password is empty")
                       @Length(min = 8, max = 50, message = "Password's length must be from 8 till 50 characters") String password) {
}
