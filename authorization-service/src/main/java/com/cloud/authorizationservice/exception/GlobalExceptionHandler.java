package com.cloud.authorizationservice.exception;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler({IllegalArgumentException.class, EntityExistsException.class, EntityNotFoundException.class,
            ResourceNotFoundException.class})
    protected ResponseEntity<?> handleBadRequest(Exception e){
        logger.warn(e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        logger.warn("Invalid format of request body");
        return ResponseEntity.badRequest().body(ex.getFieldErrors().get(0).getDefaultMessage());
    }
    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
    protected ResponseEntity<?> handleUnauthorized(Exception e){
        logger.warn(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }
}

