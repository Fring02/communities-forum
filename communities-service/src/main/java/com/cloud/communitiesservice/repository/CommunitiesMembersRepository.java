package com.cloud.communitiesservice.repository;

import com.cloud.communitiesservice.dto.base.DtoWithId;
import com.cloud.communitiesservice.dto.member.MemberRolesDto;
import com.cloud.communitiesservice.entity.CommunityMember;
import com.cloud.communitiesservice.entity.CommunityMemberRole;
import com.cloud.communitiesservice.entity.id.CommunityMemberId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommunitiesMembersRepository extends JpaRepository<CommunityMember, CommunityMemberId> {
    List<DtoWithId<CommunityMemberId>> findByRolesIn(Collection<CommunityMemberRole> roles);
    List<DtoWithId<CommunityMemberId>> findByRolesIn(Collection<CommunityMemberRole> roles, Pageable pageable);
    List<DtoWithId<CommunityMemberId>> findAllBy();
    Optional<MemberRolesDto> findByUsernameAndId_CommunityId(@Param("username") String username, @Param("communityId") long communityId);
}
