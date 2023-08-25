package com.cloud.postsservice.entity;

import com.cloud.postsservice.entity.id.ViewId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "views")
@Getter
@Setter
@NoArgsConstructor
public class View {
    @EmbeddedId
    private ViewId id;
    @MapsId("postId")
    @JoinColumn(name = "postId", referencedColumnName = "id")
    @ManyToOne
    private Post post;
    public View(UUID userId, long postId, Post post){
        this.id = new ViewId(userId, postId);
        this.post = post;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        View view = (View) o;
        return Objects.equals(id, view.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
