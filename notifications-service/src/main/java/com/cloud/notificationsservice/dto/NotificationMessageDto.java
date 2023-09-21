package com.cloud.notificationsservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.UUID;
public record NotificationMessageDto(@JsonProperty("userId") String userId, @JsonProperty("text") String text)
        implements Serializable{}
