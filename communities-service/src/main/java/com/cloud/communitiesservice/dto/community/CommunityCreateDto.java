package com.cloud.communitiesservice.dto.community;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class CommunityCreateDto {
    private String name;
    private String tag;
    private String ownerId;
    private String ownerUsername;
    private String description;
}
