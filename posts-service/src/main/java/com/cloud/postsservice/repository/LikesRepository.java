package com.cloud.postsservice.repository;

import com.cloud.postsservice.entity.Like;
import com.cloud.postsservice.entity.id.LikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikesRepository extends JpaRepository<Like, LikeId> {
    long countById_PostId(long postId);
}
