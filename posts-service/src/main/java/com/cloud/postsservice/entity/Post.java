package com.cloud.postsservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
@Getter
@Setter
@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue
    private long id;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "postedAt", nullable = false)
    private LocalDate postedAt = LocalDate.now();
    @Column(name = "ownerId", nullable = false)
    private UUID ownerId;
    @Column(name = "description", nullable = false)
    private String description;
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "post")
    private Set<View> views;
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "post")
    private Set<Like> likes;
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "post")
    private Set<Dislike> dislikes;
}
