package com.cloud.postsservice.exception;

public class EntityExistsException extends Exception {
    public EntityExistsException() {
    }

    public EntityExistsException(String message) {
        super(message);
    }
}
