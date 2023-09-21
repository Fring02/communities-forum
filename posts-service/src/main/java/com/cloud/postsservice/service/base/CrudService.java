package com.cloud.postsservice.service.base;

import com.cloud.postsservice.dto.base.DtoWithId;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

public interface CrudService<TId extends Serializable, TViewDto extends DtoWithId<TId>, TFullViewDto extends TViewDto,
        TCreateDto, TCreatedDto extends TCreateDto, TUpdateDto extends DtoWithId<TId>> {
    Collection<TViewDto> getAll();
    Page<TViewDto> getAll(int page, int pageCount);
    Optional<TFullViewDto> getById(TId id);
    boolean existsById(TId id);
    TCreatedDto create(TCreateDto userDto) throws EntityExistsException, EntityNotFoundException;
    void update(TUpdateDto dto) throws EntityNotFoundException;
    void deleteById(TId id) throws EntityNotFoundException;
}
