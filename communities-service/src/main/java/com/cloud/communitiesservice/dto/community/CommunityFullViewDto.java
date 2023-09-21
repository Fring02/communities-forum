package com.cloud.communitiesservice.dto.community;

import com.cloud.communitiesservice.dto.member.MemberDto;

import java.util.Collection;

public interface CommunityFullViewDto extends CommunityViewDto {
    Collection<? extends MemberDto> getMembers();
    Collection<? extends MemberDto> getModerators();
    long getRequiredKarma();
    long getModeratorsCount();
    Collection<? extends MemberDto> getAdmins();
    long getAdminsCount();
}
