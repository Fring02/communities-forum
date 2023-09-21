package com.cloud.communitiesservice.dto.community;

import com.cloud.communitiesservice.dto.community.CommunityViewDto;

import java.util.Set;

public interface CommunityWithCategoriesViewDto extends CommunityViewDto {
    Set<String> getCategories();
    long getRequiredKarma();
}
