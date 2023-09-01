package com.cloud.postsservice.repository;

import com.cloud.postsservice.entity.View;
import com.cloud.postsservice.entity.id.ViewId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViewsRepository extends JpaRepository<View, ViewId> {
    long countById_PostId(long postId);
}
