package com.cloud.notificationsservice.repository;

import com.cloud.notificationsservice.NotificationMessage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;

@Repository
public interface MessagesRepository extends CrudRepository<NotificationMessage, String> {
    Collection<NotificationMessage> findByUserId(String userId);
    Collection<NotificationMessage> findAllBy();
}
