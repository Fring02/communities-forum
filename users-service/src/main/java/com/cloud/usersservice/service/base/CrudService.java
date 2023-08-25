package com.cloud.usersservice.service.base;

import com.cloud.usersservice.dto.base.DtoWithId;
import com.cloud.usersservice.dto.subscribe.SubscriptionDto;
import com.cloud.usersservice.dto.user.*;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CrudService<TId extends Serializable, TViewDto extends DtoWithId<TId>, TFullViewDto extends TViewDto,
        TCreateDto, TCreatedDto extends TCreateDto, TUpdateDto extends DtoWithId<TId>> {
    List<TViewDto> getAll();
    Page<TViewDto> getAll(int page, int pageCount);
    Optional<TFullViewDto> getById(TId id);
    boolean existsById(TId id);
    TCreatedDto create(TCreateDto userDto) throws EntityExistsException, IllegalArgumentException;
    void update(TUpdateDto dto) throws EntityNotFoundException;
    void deleteById(TId id) throws EntityNotFoundException;
}
