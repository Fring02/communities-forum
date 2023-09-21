package com.cloud.communitiesservice.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
