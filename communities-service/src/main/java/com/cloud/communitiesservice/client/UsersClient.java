package com.cloud.communitiesservice.client;

import com.cloud.communitiesservice.dto.member.MemberDto;
import com.cloud.communitiesservice.dto.member.UserRolesDto;
import com.cloud.communitiesservice.dto.member.MembersListDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@FeignClient("http://users-service")
public interface UsersClient {
    @GetMapping("/api/v1/users/{id}/exists")
    boolean userExists(@PathVariable UUID id);
    @GetMapping("/api/v1/users/ids={ids}")
    Optional<MembersListDto> getByIds(@PathVariable("ids") Collection<UUID> ids);
    @GetMapping("/api/v1/users/{id}")
    ResponseEntity<MemberDto> getById(@PathVariable UUID id);
    @GetMapping("/api/v1/users/username/{username}/roles")
    ResponseEntity<UserRolesDto> getUserRoles(@PathVariable("username") String username);
}
