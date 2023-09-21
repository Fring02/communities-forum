package com.cloud.communitiesservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "roles")
@NoArgsConstructor
@Getter
@Setter
public class CommunityMemberRole {
    public CommunityMemberRole(RoleType role){
        this.name = role;
    }
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private long id;
    @Column(name = "name", nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private RoleType name;
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "roles")
    private List<CommunityMember> members;
}
