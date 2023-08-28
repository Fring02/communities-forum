package com.cloud.communitiesservice.repository;

import com.cloud.communitiesservice.entity.CommunityMemberRole;
import com.cloud.communitiesservice.entity.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRolesRepository extends JpaRepository<CommunityMemberRole, Long> {
    CommunityMemberRole findByName(RoleType name);
}
