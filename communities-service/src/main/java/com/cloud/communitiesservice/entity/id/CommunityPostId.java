package com.cloud.communitiesservice.entity.id;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommunityPostId implements Serializable {
    private long id;
    private long communityId;
}
