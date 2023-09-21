package com.cloud.communitiesservice.dto.member;

import com.cloud.communitiesservice.dto.base.DtoWithId;
import com.cloud.communitiesservice.entity.CommunityMemberRole;
import com.cloud.communitiesservice.entity.id.CommunityMemberId;

import java.util.List;
import java.util.Set;

public interface MemberRolesDto extends DtoWithId<CommunityMemberId> {
    Set<CommunityMemberRole> getRoles();
}
