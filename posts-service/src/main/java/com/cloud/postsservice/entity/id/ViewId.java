package com.cloud.postsservice.entity.id;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ViewId implements Serializable {
    private UUID userId;
    private long postId;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ViewId viewId = (ViewId) o;
        return Objects.equals(getUserId(), viewId.getUserId())
                && postId == viewId.postId;
    }
    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), postId);
    }
}
