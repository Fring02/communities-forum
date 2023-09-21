package com.cloud.communitiesservice.service;

import com.cloud.communitiesservice.dto.category.CommunityCategoriesDto;
import com.cloud.communitiesservice.dto.community.*;
import com.cloud.communitiesservice.dto.member.MemberRolesDto;
import com.cloud.communitiesservice.dto.member.NewMemberDto;
import com.cloud.communitiesservice.entity.RoleType;
import com.cloud.communitiesservice.service.base.CrudService;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface CommunitiesService extends CrudService<Long, CommunityViewDto, CommunityFullViewDto,
        CommunityCreateDto, CommunityCreatedDto, CommunityUpdateDto> {
    Collection<CommunityViewDto> getAllByTag(String tag);
    Page<CommunityViewDto> getAllByTag(String tag, int page, int pageCount);
    Collection<CommunityViewDto> getAllByName(String name);
    Page<CommunityViewDto> getAllByName(String name, int page, int pageCount);
    void addCategory(long id, String category);
    Optional<CommunityCategoriesDto> getCategoriesByCommunityId(long communityId);
    void addMember(NewMemberDto newMemberDto, long communityId);
    Optional<CommunityKarmaDto> getKarmaById(long id);
    Optional<MemberRolesDto> getRolesByUsername(long communityId, String username);
}
