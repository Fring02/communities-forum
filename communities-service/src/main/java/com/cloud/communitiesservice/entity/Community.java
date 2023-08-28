package com.cloud.communitiesservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "communities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Community {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "tag", nullable = false, unique = true)
    private String tag;
    @Column(name = "description", nullable = false)
    private String description;
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "community")
    private Set<CommunityPost> posts;
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "community")
    private Set<CommunityMember> members;
    @ElementCollection
    @CollectionTable(name = "posts_categories", joinColumns = @JoinColumn(name = "community_id"))
    @Column(name = "category_name", nullable = false)
    private Set<String> categories;
}
