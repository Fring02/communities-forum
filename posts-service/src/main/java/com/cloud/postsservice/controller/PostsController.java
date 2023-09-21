package com.cloud.postsservice.controller;

import com.cloud.postsservice.dto.post.*;
import com.cloud.postsservice.service.PostsService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@RequestMapping("/api/v1/posts")
@RestController
@CrossOrigin("http://api-gateway")
public class PostsController {
    private final PostsService service;
    public PostsController(PostsService service) {
        this.service = service;
    }
    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam("page") Optional<Integer> pageOpt,
                                    @RequestParam("pageCount") Optional<Integer> pageCountOpt,
                                    @RequestParam("communityId") Optional<Long> communityIdOpt,
                                    @RequestParam("category") Optional<String> categoryOpt){
        if(pageOpt.isPresent() && pageCountOpt.isPresent()){
            int page = pageOpt.get(), pageCount = pageCountOpt.get();
            if(page <= 0 || pageCount <= 0) return ResponseEntity.badRequest().body("Page or page count must be greater than 0");
            Page<PostViewDto> posts;
            if(communityIdOpt.isPresent() && categoryOpt.isPresent())
                posts = service.getAll(communityIdOpt.get(), categoryOpt.get(), page, pageCount);
            else if(communityIdOpt.isPresent()) posts = service.getAll(communityIdOpt.get(), null, page, pageCount);
            else posts = service.getAll(page, pageCount);
            return ResponseEntity.ok(new PostsPageDto(posts.toList(), posts.getTotalElements(), posts.getTotalPages()));
        }
        Collection<PostViewDto> posts;
        if(communityIdOpt.isPresent() && categoryOpt.isPresent())
            posts = service.getAll(communityIdOpt.get(), categoryOpt.get());
        else posts = service.getAll();
        return ResponseEntity.ok(new PostsListDto(posts, posts.size()));
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") long id) {
        if(id <= 0) return ResponseEntity.badRequest().body("Id is invalid");
        return ResponseEntity.of(service.getById(id));
    }
    @PostMapping
    @RolesAllowed("user")
    public ResponseEntity<PostCreatedDto> addPost(@RequestBody @Valid PostCreateDto dto, HttpServletRequest request)
            throws EntityNotFoundException, EntityExistsException {
        var newPost = service.create(dto);
        return ResponseEntity.created(URI.create(request.getRequestURI())).body(newPost);
    }
    @PatchMapping("/{id}")
    @RolesAllowed("user")
    @PreAuthorize("@postsAuthorizationHandler.authorizeOwnerOnPost(#id, authentication)")
    public ResponseEntity<?> updateById(@PathVariable("id") long id, @RequestBody PostUpdateDto dto) throws EntityNotFoundException {
        if(id <= 0) return ResponseEntity.badRequest().body("Id is invalid");
        dto.setId(id);
        service.update(dto);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/{id}")
    @RolesAllowed({"user", "moderator"})
    @PreAuthorize("@postsAuthorizationHandler.authorizeOwnerOrModeratorOnPost(#id, authentication, {'user', 'moderator'})")
    public ResponseEntity<?> deleteById(@PathVariable("id") long id) throws EntityNotFoundException {
        if(id <= 0) return ResponseEntity.badRequest().body("Id is invalid");
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
    @PatchMapping("/{id}/views")
    public ResponseEntity<?> updateViewsCountForPost(@PathVariable("id") long id, @RequestBody UUID userId) throws EntityNotFoundException {
        if(id <= 0) return ResponseEntity.badRequest().body("Id is invalid");
        return ResponseEntity.ok(service.updateViewsCount(id, userId));
    }
    @PatchMapping("/{id}/likes")
    @RolesAllowed("user")
    public ResponseEntity<?> updateLikesCountForPost(@PathVariable("id") long id, @RequestBody UUID userId) throws EntityNotFoundException {
        if(id <= 0) return ResponseEntity.badRequest().body("Id is invalid");
        service.updateLikesCount(id, userId);
        return ResponseEntity.ok().build();
    }
    @PatchMapping("/{id}/dislikes")
    @RolesAllowed("user")
    public ResponseEntity<?> updateDislikesCountForPost(@PathVariable("id") long id, @RequestBody UUID userId) throws EntityNotFoundException {
        if(id <= 0) return ResponseEntity.badRequest().body("Id is invalid");
        service.updateDislikesCount(id, userId);
        return ResponseEntity.ok().build();
    }
}
