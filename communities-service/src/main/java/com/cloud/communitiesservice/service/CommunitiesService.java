package com.cloud.communitiesservice.service;

import com.cloud.communitiesservice.dto.community.*;
import com.cloud.communitiesservice.entity.RoleType;
import com.cloud.communitiesservice.service.base.CrudService;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.UUID;

public interface CommunitiesService extends CrudService<Long, CommunityViewDto, CommunityFullViewDto,
        CommunityCreateDto, CommunityCreatedDto, CommunityUpdateDto> {
    Collection<CommunityViewDto> getAllByTag(String tag);
    Page<CommunityViewDto> getAllByTag(String tag, int page, int pageCount);
    Collection<CommunityViewDto> getAllByName(String name);
    Page<CommunityViewDto> getAllByName(String name, int page, int pageCount);
    void addCategory(long id, String category);
    void addMember(RoleType roleType, UUID memberId, long communityId);
}
