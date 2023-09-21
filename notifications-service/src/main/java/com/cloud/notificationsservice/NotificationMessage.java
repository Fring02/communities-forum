package com.cloud.notificationsservice;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.UUID;


@Getter
@Setter
@RedisHash
public class NotificationMessage implements Serializable{
    @Id
    private String id;
    @Indexed
    private String userId;
    private String text;
}
