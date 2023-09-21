package com.cloud.postsservice.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Getter
@Setter
@AllArgsConstructor
public class NotificationMessageDto implements Serializable {
    private String userId;
    private String text;
}
