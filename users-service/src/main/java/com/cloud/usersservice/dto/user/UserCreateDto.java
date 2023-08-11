package com.cloud.usersservice.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
@Getter
@Setter
public class UserCreateDto {
    @NotBlank(message = "First name is empty")
    private String firstName;
    @NotBlank(message = "Last name is empty")
    private String lastName;
    @Email(message = "Email is of invalid format")
    private String email;
    @NotBlank(message = "Username is empty")
    @Length(min = 8, max = 20, message = "Username's length bounds should be from 8 to 20 characters")
    private String userName;
}
