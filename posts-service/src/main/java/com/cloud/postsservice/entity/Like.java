package com.cloud.postsservice.entity;

import com.cloud.postsservice.entity.id.LikeId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "likes")
@Getter
@Setter
@NoArgsConstructor
public class Like {
    @EmbeddedId
    private LikeId id;
    @MapsId("postId")
    @JoinColumn(name = "postId", referencedColumnName = "id")
    @ManyToOne
    private Post post;
    public Like(UUID userId, long postId, Post post){
        this.id = new LikeId(userId, postId);
        this.post = post;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Like like = (Like) o;
        return Objects.equals(id, like.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
