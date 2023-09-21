package com.cloud.notificationsservice.controller;

import com.cloud.notificationsservice.NotificationMessage;
import com.cloud.notificationsservice.dto.NotificationMessageDto;
import com.cloud.notificationsservice.repository.MessagesRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/notifications")
@CrossOrigin("http://api-gateway")
public class NotificationsController {
    private final MessagesRepository repository;
    public NotificationsController(MessagesRepository repository) {
        this.repository = repository;
    }
    @RabbitListener(queues = "${notifications.exchange.name}")
    public void consumeNotification(final NotificationMessageDto message){
        var redisMessage = new NotificationMessage();
        redisMessage.setUserId(message.userId());
        redisMessage.setText(message.text());
        repository.save(redisMessage);
    }
    @GetMapping
    public Collection<NotificationMessage> getAllNotifications(@RequestParam Optional<String> userId){
        if(userId.isEmpty()) return Collections.emptyList();
        return repository.findByUserId(userId.get());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable String id){
        if(!StringUtils.hasLength(id)) return ResponseEntity.badRequest().build();
        if(!repository.existsById(id)) return ResponseEntity.badRequest().body("Id doesn't exist");
        repository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
