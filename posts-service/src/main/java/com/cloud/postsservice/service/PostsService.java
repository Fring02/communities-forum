package com.cloud.postsservice.service;

import com.cloud.postsservice.dto.*;
import com.cloud.postsservice.service.base.CrudService;
import jakarta.persistence.EntityNotFoundException;

import java.util.UUID;

public interface PostsService extends CrudService<Long, PostViewDto, PostFullViewDto,
        PostCreateDto, PostCreatedDto, PostUpdateDto> {
    long updateViewsCount(long postId, UUID userId) throws EntityNotFoundException;
    void updateLikesCount(long postId, UUID userId) throws EntityNotFoundException;
    void updateDislikesCount(long postId, UUID userId) throws EntityNotFoundException;
}
