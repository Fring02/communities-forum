package com.cloud.communitiesservice.entity;

import com.cloud.communitiesservice.entity.id.CommunityMemberId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "community_members")
@NoArgsConstructor
@Getter
@Setter
public class CommunityMember {
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "userId", column = @Column(name = "user_id", nullable = false)),
            @AttributeOverride(name = "communityId", column = @Column(name = "community_id", nullable = false))
    })
    private CommunityMemberId id;
    @MapsId("community_id")
    @JoinColumn(name = "community_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Community community;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(name = "users_roles")
    private List<CommunityMemberRole> roles;
    public CommunityMember(UUID id, long communityId){
        this.id = new CommunityMemberId(id, communityId);
    }
}
