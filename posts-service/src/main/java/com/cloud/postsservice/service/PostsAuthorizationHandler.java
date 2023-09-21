package com.cloud.postsservice.service;

import com.cloud.postsservice.client.CommunitiesClient;
import com.cloud.postsservice.dto.post.PostOwnerDto;
import com.cloud.postsservice.repository.PostsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class PostsAuthorizationHandler {
    private final PostsRepository repository;
    private final CommunitiesClient client;
    private final Logger logger = LoggerFactory.getLogger(PostsAuthorizationHandler.class);
    public PostsAuthorizationHandler(PostsRepository repository, CommunitiesClient client) {
        this.repository = repository;
        this.client = client;
    }
    public boolean authorizeOwnerOnPost(long postId, Authentication authentication){
        UserDetails u = (UserDetails) authentication.getDetails();
        logger.info("Trying to authorize post owner...");
        var postOwnerOpt = repository.findById(postId, PostOwnerDto.class);
        return postOwnerOpt.filter(postOwnerDto -> Objects.equals(postOwnerDto.getOwnerUsername(), u.getUsername())).isPresent();
    }
    public boolean authorizeOwnerOrModeratorOnPost(long postId, Authentication authentication, List<String> allowedRoles){
        allowedRoles = allowedRoles.stream().map(String::toUpperCase).toList();
        UserDetails u = (UserDetails) authentication.getDetails();
        logger.info("Trying to authorize user's roles with allowed roles...");
        var post = repository.findById(postId);
        if(post.isEmpty()) return false;
        var communityId = post.get().getCommunityId();
        var userRoles = client.getCommunityRoles(communityId, u.getUsername());
        if(userRoles.isEmpty()) return false;
        return post.filter(p -> Objects.equals(p.getOwnerUsername(), u.getUsername())).isPresent() ||
                !Collections.disjoint(userRoles, allowedRoles);
    }
}
