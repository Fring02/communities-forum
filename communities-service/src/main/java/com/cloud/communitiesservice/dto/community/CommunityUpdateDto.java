package com.cloud.communitiesservice.dto.community;

import com.cloud.communitiesservice.dto.DtoWithId;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommunityUpdateDto implements DtoWithId<Long> {
    private long id;
    private String name;
    private String tag;
    private String description;
    @Override
    public Long getId() {
        return id;
    }
}
