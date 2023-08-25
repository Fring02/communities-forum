package com.cloud.postsservice.repository;

import com.cloud.postsservice.entity.Post;
import com.cloud.postsservice.repository.base.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostsRepository extends BaseRepository<Post, Long> {
    boolean existsById(long id);
    boolean existsByTitle(String title);
}
