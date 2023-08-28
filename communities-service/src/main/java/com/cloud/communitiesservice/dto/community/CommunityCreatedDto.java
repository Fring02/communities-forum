package com.cloud.communitiesservice.dto.community;

import com.cloud.communitiesservice.dto.DtoWithId;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommunityCreatedDto extends CommunityCreateDto implements DtoWithId<Long> {
    private long id;
    @Override
    public Long getId() {
        return id;
    }
}
