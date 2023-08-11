package com.cloud.usersservice.controller;

import com.cloud.usersservice.dto.subscribe.SubscriptionDto;
import com.cloud.usersservice.dto.user.UserCreateDto;
import com.cloud.usersservice.dto.user.UserUpdateDto;
import com.cloud.usersservice.dto.user.UsersListDto;
import com.cloud.usersservice.dto.user.UsersPageDto;
import com.cloud.usersservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UsersController {
    private final UserService service;
    private final Logger logger;
    public UsersController(UserService service) {
        this.service = Objects.requireNonNull(service);
        logger = LoggerFactory.getLogger(UsersController.class);
    }
    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam("page") Optional<Integer> pageOpt, @RequestParam("pageCount") Optional<Integer> pageCountOpt){
        if(pageOpt.isPresent() && pageCountOpt.isPresent()){
            int page = pageOpt.get(), pageCount = pageCountOpt.get();
            if(page <= 0 || pageCount <= 0) return ResponseEntity.badRequest().body("Page or page count must be greater than 0");
            var users = service.getAll(page, pageCount);
            return ResponseEntity.ok(new UsersPageDto(users.toList(), users.getTotalElements(), users.getTotalPages()));
        }
        var users = service.getAll();
        int usersCount = users.size();
        return ResponseEntity.ok(new UsersListDto(users, usersCount));
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") String id){
        if(!StringUtils.hasLength(id)) return ResponseEntity.badRequest().body("Id is invalid");
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e){
            logger.warn("Id is not of UUID format");
            return ResponseEntity.badRequest().body("Id is invalid");
        }
        return ResponseEntity.ok(service.getById(uuid));
    }
    @GetMapping("/username/{username}/roles")
    public ResponseEntity<?> getByUsername(@PathVariable String username){
        if(!StringUtils.hasLength(username)) return ResponseEntity.badRequest().body("Username is invalid");
        return ResponseEntity.ok(service.getRolesByUsername(username));
    }
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody @Valid UserCreateDto dto, HttpServletRequest request){
        var createdUser = service.create(dto);
        return ResponseEntity.created(URI.create(request.getRequestURI())).body(createdUser);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateById(@PathVariable("id") String id, @RequestBody UserUpdateDto dto){
        if(!StringUtils.hasLength(id)) return ResponseEntity.badRequest().body("Id is invalid");
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e){
            logger.warn("Id is not of UUID format");
            return ResponseEntity.badRequest().body("Id is invalid");
        }
        dto.setId(uuid);
        service.update(dto);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") String id){
        if(!StringUtils.hasLength(id)) return ResponseEntity.badRequest().body("Id is invalid");
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e){
            logger.warn("Id is not of UUID format");
            return ResponseEntity.badRequest().body("Id is invalid");
        }
        service.deleteById(uuid);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody @Valid SubscriptionDto dto){
        service.subscribe(dto);
        return ResponseEntity.ok().build();
    }
}
