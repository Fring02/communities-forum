package com.cloud.postsservice.entity;

import com.cloud.postsservice.entity.id.DislikeId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "dislikes")
@Getter
@Setter
@NoArgsConstructor
public class Dislike {
    @EmbeddedId
    private DislikeId id;
    @MapsId("postId")
    @JoinColumn(name = "postId", referencedColumnName = "id")
    @ManyToOne
    private Post post;
    public Dislike(UUID userId, long postId, Post post){
        this.id = new DislikeId(postId, userId);
        this.post = post;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dislike dislike = (Dislike) o;
        return Objects.equals(id, dislike.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
