package com.cloud.postsservice.service.base;

import com.cloud.postsservice.dto.DtoWithId;
import com.cloud.postsservice.exception.EntityExistsException;
import com.cloud.postsservice.exception.EntityNotFoundException;
import com.cloud.postsservice.exception.UserNotFoundException;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface CrudService<TId extends Serializable, TViewDto extends DtoWithId<TId>, TFullViewDto extends TViewDto,
        TCreateDto, TCreatedDto extends TCreateDto, TUpdateDto extends DtoWithId<TId>> {
    List<TViewDto> getAll();
    Page<TViewDto> getAll(int page, int pageCount);
    Optional<TFullViewDto> getById(TId id);
    boolean existsById(TId id);
    TCreatedDto create(TCreateDto userDto) throws EntityExistsException, EntityNotFoundException;
    void update(TUpdateDto dto) throws EntityNotFoundException;
    void deleteById(TId id) throws EntityNotFoundException;
}
