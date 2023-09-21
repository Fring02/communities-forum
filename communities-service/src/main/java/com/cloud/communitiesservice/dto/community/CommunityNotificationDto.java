package com.cloud.communitiesservice.dto.community;

import com.cloud.communitiesservice.dto.base.DtoWithId;
import lombok.Getter;
import lombok.Setter;

public class CommunityNotificationDto implements DtoWithId<Long> {
    @Getter
    @Setter
    private String name;
    private long id;
    public CommunityNotificationDto(long id, String name){
        this.id = id;
        this.name = name;
    }
    @Override
    public Long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
}
