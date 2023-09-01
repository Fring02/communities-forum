package com.cloud.postsservice.repository;

import com.cloud.postsservice.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface CategoriesRepository extends JpaRepository<Category, UUID> {
    Category findByNameAndCommunityId(String name, long communityId);
    boolean existsByCommunityIdAndName(long communityId, String name);
}
