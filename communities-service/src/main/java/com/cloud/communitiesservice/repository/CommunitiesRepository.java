package com.cloud.communitiesservice.repository;

import com.cloud.communitiesservice.dto.community.CommunityNotificationDto;
import com.cloud.communitiesservice.dto.community.CommunityViewDto;
import com.cloud.communitiesservice.entity.Community;
import com.cloud.communitiesservice.repository.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface CommunitiesRepository extends BaseRepository<Community, Long> {
    boolean existsByTagOrName(String tag, String name);
    Collection<CommunityViewDto> findAllByTagContainingIgnoreCase(String tag);
    Page<CommunityViewDto> findAllByTagContainingIgnoreCase(String tag, Pageable pageable);
    Collection<CommunityViewDto> findAllByNameContaining(String name);
    Page<CommunityViewDto> findAllByNameContaining(String name, Pageable pageable);
    @Query("select new com.cloud.communitiesservice.dto.community.CommunityNotificationDto(c.id, c.name) from Community c " +
            "inner join c.members m " +
            "where count(m) = (select max(count(m.id)) from CommunityMember m group by m.community.id)")
    CommunityNotificationDto findByMembersCount();
}
