package com.cloud.communitiesservice.service;

import com.cloud.communitiesservice.repository.CommunitiesMembersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class CommunitiesAuthorizationHandler {
    private final CommunitiesMembersRepository membersRepository;
    private final Logger logger = LoggerFactory.getLogger(CommunitiesAuthorizationHandler.class);
    public CommunitiesAuthorizationHandler(CommunitiesMembersRepository membersRepository) {
        this.membersRepository = membersRepository;
    }
    public boolean authorizeUserOnCommunity(long communityId, Authentication authentication, List<String> allowedRoles){
        allowedRoles = allowedRoles.stream().map(String::toUpperCase).toList();
        UserDetails u = (UserDetails) authentication.getDetails();
        logger.info("Trying to authorize user's roles with allowed roles...");
        var userRolesOpt = membersRepository.findByUsernameAndId_CommunityId(u.getUsername(), communityId);
        if(userRolesOpt.isEmpty()) {
            logger.warn("Access denied for user " + u.getUsername() + " for community " + communityId);
            return false;
        }
        var userRoles = userRolesOpt.get().getRoles().stream().map(r -> r.getName().name()).toList();
        return !Collections.disjoint(userRoles, allowedRoles);
    }
}
