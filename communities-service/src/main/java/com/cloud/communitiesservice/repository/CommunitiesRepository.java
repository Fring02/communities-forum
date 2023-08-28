package com.cloud.communitiesservice.repository;

import com.cloud.communitiesservice.dto.community.CommunityViewDto;
import com.cloud.communitiesservice.entity.Community;
import com.cloud.communitiesservice.repository.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface CommunitiesRepository extends BaseRepository<Community, Long> {
    boolean existsByTagOrName(String tag, String name);
    Collection<CommunityViewDto> findAllByTagContainingIgnoreCase(String tag);
    Page<CommunityViewDto> findAllByTagContainingIgnoreCase(String tag, Pageable pageable);
    Collection<CommunityViewDto> findAllByNameContaining(String name);
    Page<CommunityViewDto> findAllByNameContaining(String name, Pageable pageable);
}
