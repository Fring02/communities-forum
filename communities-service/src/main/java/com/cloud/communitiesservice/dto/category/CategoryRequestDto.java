package com.cloud.communitiesservice.dto.category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CategoryRequestDto {
    private long communityId;
    private String category;
}
