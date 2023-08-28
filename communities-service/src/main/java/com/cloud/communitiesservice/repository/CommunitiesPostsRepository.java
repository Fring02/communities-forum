package com.cloud.communitiesservice.repository;

import com.cloud.communitiesservice.entity.CommunityPost;
import com.cloud.communitiesservice.entity.id.CommunityPostId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunitiesPostsRepository extends JpaRepository<CommunityPost, CommunityPostId> {
}
