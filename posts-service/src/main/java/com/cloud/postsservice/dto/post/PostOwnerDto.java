package com.cloud.postsservice.dto.post;

import com.cloud.postsservice.dto.base.DtoWithId;


public interface PostOwnerDto extends DtoWithId<Long> {
    String getOwnerUsername();
}
