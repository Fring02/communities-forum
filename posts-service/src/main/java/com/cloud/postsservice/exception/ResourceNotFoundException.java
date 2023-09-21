package com.cloud.postsservice.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ResourceNotFoundException extends EntityNotFoundException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
