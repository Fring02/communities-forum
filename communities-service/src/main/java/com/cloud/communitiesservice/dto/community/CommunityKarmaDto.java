package com.cloud.communitiesservice.dto.community;

import com.cloud.communitiesservice.dto.base.DtoWithId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public interface CommunityKarmaDto extends DtoWithId<Long> {
    long getRequiredKarma();
}
