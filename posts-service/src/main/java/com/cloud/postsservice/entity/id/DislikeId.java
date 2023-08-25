package com.cloud.postsservice.entity.id;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DislikeId implements Serializable {
    private long postId;
    private UUID userId;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DislikeId dislikeId = (DislikeId) o;
        return Objects.equals(postId, dislikeId.postId) && Objects.equals(userId, dislikeId.userId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(postId, userId);
    }
}
