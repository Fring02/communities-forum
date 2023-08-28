package com.cloud.communitiesservice.dto.community;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
@AllArgsConstructor
@Getter
@Setter
public class CommunitiesListDto {
    private final Collection<CommunityViewDto> posts;
    private final long count;
}
