package com.cloud.communitiesservice.controller;

import com.cloud.communitiesservice.dto.community.*;
import com.cloud.communitiesservice.dto.member.NewMemberDto;
import com.cloud.communitiesservice.service.CommunitiesService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/communities")
@CrossOrigin("http://api-gateway")
public class CommunitiesController {
    private final CommunitiesService service;
    public CommunitiesController(CommunitiesService service) {
        this.service = Objects.requireNonNull(service);
    }
    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam("page") Optional<Integer> pageOpt,
                                    @RequestParam("pageCount") Optional<Integer> pageCountOpt,
                                    @RequestParam("tag") Optional<String> tagOpt,
                                    @RequestParam("name") Optional<String> nameOpt){
        if(pageOpt.isPresent() && pageCountOpt.isPresent()){
            int page = pageOpt.get(), pageCount = pageCountOpt.get();
            if(page <= 0 || pageCount <= 0) return ResponseEntity.badRequest().body("Page or page count must be greater than 0");
            Page<CommunityViewDto> communities;
            if(tagOpt.isPresent()) communities = service.getAllByTag(tagOpt.get(), page, pageCount);
            else if(nameOpt.isPresent()) communities = service.getAllByName(nameOpt.get(), page, pageCount);
            else communities = service.getAll(page, pageCount);
            return ResponseEntity.ok(new CommunitiesPageDto(communities.toList(), communities.getTotalElements(), communities.getTotalPages()));
        }
        var communities = service.getAll();
        int usersCount = communities.size();
        return ResponseEntity.ok(new CommunitiesListDto(communities, usersCount));
    }
    @GetMapping("/{id}")
    @Cacheable(key = "#id", value = "communities")
    public ResponseEntity<?> getById(@PathVariable("id") long id, @RequestParam Optional<Boolean> karmaOnly) {
        if(id <= 0) return ResponseEntity.badRequest().body("Id is invalid");
        if(karmaOnly.isPresent() && karmaOnly.get()) {
            var karma = service.getKarmaById(id);
            if(karma.isEmpty()) return ResponseEntity.noContent().build();
            return ResponseEntity.ok(karma.get().getRequiredKarma());
        }
        return ResponseEntity.of(service.getById(id));
    }
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> checkIfCommunityExists(@PathVariable long id){
        if(id <= 0) return ResponseEntity.badRequest().body(false);
        return ResponseEntity.ok(service.existsById(id));
    }
    @GetMapping("/{id}/categories")
    @CrossOrigin("http://posts-service")
    public Collection<String> getCommunityCategories(@PathVariable("id") long id){
        if(id <= 0) return Collections.emptyList();
        var categories = service.getCategoriesByCommunityId(id);
        if(categories.isEmpty()) return Collections.emptyList();
        return categories.get().getCategories().stream().map(c -> c.replaceAll("\"", ""))
                .collect(Collectors.toSet());
    }
    @PostMapping("/{id}/categories")
    @PreAuthorize("@communitiesAuthorizationHandler.authorizeUserOnCommunity(#id, authentication, {'admin', 'moderator'})")
    public ResponseEntity<?> addCommunityCategories(@PathVariable("id") long id, @RequestBody String category){
        if(id <= 0) return ResponseEntity.badRequest().body("Id is invalid");
        service.addCategory(id, category);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/{id}/members")
    @PreAuthorize("@communitiesAuthorizationHandler.authorizeUserOnCommunity(#id, authentication, {'admin', 'moderator'})")
    public ResponseEntity<?> addCommunityMember(@PathVariable("id") long id, @RequestBody NewMemberDto memberDto){
        service.addMember(memberDto, id);
        return ResponseEntity.ok("New member added");
    }
    @PostMapping
    @RolesAllowed("user")
    public ResponseEntity<CommunityCreatedDto> createCommunity(@RequestBody @Valid CommunityCreateDto dto, HttpServletRequest request)
            throws EntityNotFoundException, EntityExistsException {
        var newPost = service.create(dto);
        return ResponseEntity.created(URI.create(request.getRequestURI())).body(newPost);
    }
    @PatchMapping("/{id}")
    @PreAuthorize("@communitiesAuthorizationHandler.authorizeUserOnCommunity(#id, authentication, {'admin'})")
    public ResponseEntity<?> updateById(@PathVariable("id") long id, @RequestBody CommunityUpdateDto dto) throws EntityNotFoundException {
        if(id <= 0) return ResponseEntity.badRequest().body("Id is invalid");
        dto.setId(id);
        service.update(dto);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("@communitiesAuthorizationHandler.authorizeUserOnCommunity(#id, authentication, {'admin'})")
    @CacheEvict(key = "#id", value = "communities")
    public ResponseEntity<?> deleteById(@PathVariable("id") long id) throws EntityNotFoundException {
        if(id <= 0) return ResponseEntity.badRequest().body("Id is invalid");
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/{id}/members/{username}/roles")
    @CrossOrigin("http://posts-service")
    public Set<String> getCommunityRolesByUsername(@PathVariable String username, @PathVariable long id){
        if(id <= 0 || !StringUtils.hasLength(username)) return Set.of();
        var roles = service.getRolesByUsername(id, username);
        return roles.map(memberRolesDto -> memberRolesDto.getRoles().stream().map(r ->
                r.getName().name().toLowerCase()).collect(Collectors.toSet())).orElseGet(Set::of);
    }
}
