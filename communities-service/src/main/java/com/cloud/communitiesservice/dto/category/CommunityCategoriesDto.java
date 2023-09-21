package com.cloud.communitiesservice.dto.category;

import com.cloud.communitiesservice.dto.base.DtoWithId;

import java.util.Set;

public interface CommunityCategoriesDto extends DtoWithId<Long> {
    Set<String> getCategories();
}
