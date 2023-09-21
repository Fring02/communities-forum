package com.cloud.communitiesservice.service;

import com.cloud.communitiesservice.dto.misc.NotificationMessageDto;
import com.cloud.communitiesservice.repository.CommunitiesMembersRepository;
import com.cloud.communitiesservice.repository.CommunitiesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableAsync
@Component
public class CommunitiesNotificationsService {
    private final RabbitTemplate rabbitTemplate;
    private final CommunitiesRepository repository;
    private final CommunitiesMembersRepository membersRepository;
    private final Logger logger = LoggerFactory.getLogger(CommunitiesNotificationsService.class);
    @Value("${notifications.exchange.name}")
    private String exchangeName;
    public CommunitiesNotificationsService(RabbitTemplate rabbitTemplate, CommunitiesRepository repository,
                                           CommunitiesMembersRepository membersRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.repository = repository;
        this.membersRepository = membersRepository;
    }
    @Async
    @Scheduled(cron = "0 0 11 * * *")
    public void notifyRandomCommunity(){
        var topMembersCommunity = repository.findByMembersCount();
        var userIds = membersRepository.findAllBy();
        logger.info("Scheduled activity: notifying all users about popular community...");
        userIds.forEach(id -> rabbitTemplate.convertAndSend(exchangeName,
                new NotificationMessageDto(id.getId().getUserId().toString(),
                        "The community you might be interested in: " + topMembersCommunity.getName())));
        logger.info("Scheduled activity: done.");
    }
}
