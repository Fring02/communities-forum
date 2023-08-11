package com.cloud.usersservice.dto.base;

import java.io.Serializable;
public interface DtoWithId<TID extends Serializable> {
    TID getId();
}