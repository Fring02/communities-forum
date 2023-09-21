package com.cloud.communitiesservice.dto.community;

import com.cloud.communitiesservice.dto.base.DtoWithId;
import org.springframework.beans.factory.annotation.Value;


public interface CommunityViewDto extends DtoWithId<Long> {
    String getName();
    String getTag();
    String getDescription();
    @Value("#{target.members.size()}")
    long getMembersCount();
}
