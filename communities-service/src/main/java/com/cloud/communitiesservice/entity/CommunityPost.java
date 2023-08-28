package com.cloud.communitiesservice.entity;

import com.cloud.communitiesservice.entity.id.CommunityPostId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "posts_communities")
@Getter
@Setter
public class CommunityPost {
    @EmbeddedId
    private CommunityPostId id;
    @MapsId("communityId")
    @JoinColumn(name = "communityId", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Community community;
}
