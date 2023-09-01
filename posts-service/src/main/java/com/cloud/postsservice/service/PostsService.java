package com.cloud.postsservice.service;

import com.cloud.postsservice.dto.*;
import com.cloud.postsservice.service.base.CrudService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface PostsService extends CrudService<Long, PostViewDto, PostFullViewDto,
        PostCreateDto, PostCreatedDto, PostUpdateDto> {
    Page<PostViewDto> getAll(long communityId, String category, int page, int pageCount);
    Collection<PostViewDto> getAll(long communityId, String category);
    long updateViewsCount(long postId, UUID userId) throws EntityNotFoundException;
    void updateLikesCount(long postId, UUID userId) throws EntityNotFoundException;
    void updateDislikesCount(long postId, UUID userId) throws EntityNotFoundException;
}
