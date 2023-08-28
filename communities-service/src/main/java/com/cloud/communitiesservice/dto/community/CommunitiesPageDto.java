package com.cloud.communitiesservice.dto.community;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
@Getter
@Setter
public class CommunitiesPageDto extends CommunitiesListDto {
    private final int pagesCount;
    public CommunitiesPageDto(Collection<CommunityViewDto> users, long count, int pagesCount) {
        super(users, count);
        this.pagesCount = pagesCount;
    }
}
