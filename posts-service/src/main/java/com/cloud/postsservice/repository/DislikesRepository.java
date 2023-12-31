package com.cloud.postsservice.repository;

import com.cloud.postsservice.entity.Dislike;
import com.cloud.postsservice.entity.id.DislikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DislikesRepository extends JpaRepository<Dislike, DislikeId> {
    long countById_PostId(long postId);
}
