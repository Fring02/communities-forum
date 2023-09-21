package com.cloud.usersservice.dto.misc;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record NotificationMessageDto(@JsonProperty("userId") String userId, @JsonProperty("text") String text)
        implements Serializable{}
