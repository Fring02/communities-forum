package com.cloud.communitiesservice.util;

import com.cloud.communitiesservice.entity.CommunityMemberRole;
import com.cloud.communitiesservice.entity.RoleType;
import com.cloud.communitiesservice.repository.CommunityRolesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public final class DatabaseSeeder {
    private final CommunityRolesRepository rolesRepository;
    private final Logger logger;
    public DatabaseSeeder(CommunityRolesRepository rolesRepository) {
        this.rolesRepository = rolesRepository;
        this.logger = LoggerFactory.getLogger(DatabaseSeeder.class);
    }
    public void seedRoles(){
        logger.info("Seeding roles...");
        rolesRepository.saveAll(List.of(new CommunityMemberRole(RoleType.SUPER_ADMIN),new CommunityMemberRole(RoleType.ADMIN),
                new CommunityMemberRole(RoleType.MODERATOR), new CommunityMemberRole(RoleType.USER)));
    }
}
