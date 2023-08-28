package com.cloud.communitiesservice.controller;

import com.cloud.communitiesservice.dto.community.*;
import com.cloud.communitiesservice.dto.member.NewMemberDto;
import com.cloud.communitiesservice.entity.RoleType;
import com.cloud.communitiesservice.service.CommunitiesService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/communities")
public class CommunitiesController {
    private final CommunitiesService service;
    public CommunitiesController(CommunitiesService service) {
        this.service = service;
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
        var posts = service.getAll();
        int usersCount = posts.size();
        return ResponseEntity.ok(new CommunitiesListDto(posts, usersCount));
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") long id) {
        if(id <= 0) return ResponseEntity.badRequest().body("Id is invalid");
        return ResponseEntity.of(service.getById(id));
    }
    @PostMapping("/{id}/categories")
    public ResponseEntity<?> addCommunityCategories(@PathVariable("id") long id, @RequestBody String category){
        if(id <= 0) return ResponseEntity.badRequest().body("Id is invalid");
        service.addCategory(id, category);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/{id}/members")
    public ResponseEntity<?> addCommunityMember(@PathVariable("id") long id, @RequestBody NewMemberDto memberDto){
        if(memberDto.getUserId() == null) return ResponseEntity.badRequest().body("User id not provided");
        RoleType roleType = Enum.valueOf(RoleType.class, memberDto.getRole());
        service.addMember(roleType, memberDto.getUserId(), id);
        return ResponseEntity.ok("New member added");
    }
    @PostMapping
    public ResponseEntity<CommunityCreatedDto> createCommunity(@RequestBody @Valid CommunityCreateDto dto, HttpServletRequest request)
            throws EntityNotFoundException, EntityExistsException {
        var newPost = service.create(dto);
        return ResponseEntity.created(URI.create(request.getRequestURI())).body(newPost);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateById(@PathVariable("id") long id, @RequestBody CommunityUpdateDto dto) throws EntityNotFoundException {
        if(id <= 0) return ResponseEntity.badRequest().body("Id is invalid");
        dto.setId(id);
        service.update(dto);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") long id) throws EntityNotFoundException {
        if(id <= 0) return ResponseEntity.badRequest().body("Id is invalid");
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
