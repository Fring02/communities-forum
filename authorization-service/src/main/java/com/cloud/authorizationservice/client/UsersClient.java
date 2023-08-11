package com.cloud.authorizationservice.client;

import com.cloud.authorizationservice.dto.RegisterDto;
import com.cloud.authorizationservice.dto.UserRolesDto;
import com.cloud.authorizationservice.dto.UserWithRolesDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("http://users-service")
public interface UsersClient {
    @PostMapping("/api/users")
    ResponseEntity<UserWithRolesDto> createUser(@RequestBody RegisterDto registerDto);
    @GetMapping("/api/users/username/{username}/roles")
    ResponseEntity<UserRolesDto> getUserRoles(@PathVariable("username") String username);
}
