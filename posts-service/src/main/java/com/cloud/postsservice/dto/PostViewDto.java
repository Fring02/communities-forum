package com.cloud.postsservice.dto;

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.util.UUID;

public interface PostViewDto extends DtoWithId<Long> {
    String getTitle();
    @Value("#{target.postedAt.toString()}")
    String getPostedAt();
    @Value("#{target.views.size()}")
    long getViewCount();
    @Value("#{target.likes.size()}")
    int getLikesCount();
    @Value("#{target.dislikes.size()}")
    int getDislikesCount();
    @Value("#{target.category.name}")
    String getCategoryName();
    long getCommunityId();
    UUID getOwnerId();
}
