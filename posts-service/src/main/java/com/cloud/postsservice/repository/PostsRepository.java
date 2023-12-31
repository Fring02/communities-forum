package com.cloud.postsservice.repository;

import com.cloud.postsservice.dto.post.PostViewDto;
import com.cloud.postsservice.entity.Post;
import com.cloud.postsservice.repository.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface PostsRepository extends BaseRepository<Post, Long> {
    Page<PostViewDto> findByCommunityId(long communityId, Pageable pageable);
    Page<PostViewDto> findByCommunityIdAndCategory(long communityId, String categoryName, Pageable pageable);
    Collection<PostViewDto> findByCommunityIdAndCategory(long communityId, String categoryName);
    Collection<PostViewDto> findByCommunityId(long communityId);
    boolean existsByTitle(String title);
}
