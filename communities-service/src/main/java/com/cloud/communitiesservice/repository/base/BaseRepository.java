package com.cloud.communitiesservice.repository.base;

import com.cloud.communitiesservice.dto.DtoWithId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<TEntity, TID extends Serializable> extends JpaRepository<TEntity, TID> {
    <TViewDto extends DtoWithId<TID>> Collection<TViewDto> findBy(Class<TViewDto> type);
    <TViewDto extends DtoWithId<TID>> Collection<TViewDto> findBy(Class<TViewDto> type, Sort sort);
    <TViewDto extends DtoWithId<TID>> Page<TViewDto> findBy(Class<TViewDto> type, Pageable pageable);
    <TViewDto> Optional<TViewDto> findById(TID id, Class<TViewDto> type);
}
