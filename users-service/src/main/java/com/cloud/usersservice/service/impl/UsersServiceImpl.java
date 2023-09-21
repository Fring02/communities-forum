package com.cloud.usersservice.service.impl;

import com.cloud.usersservice.dto.subscribe.SubscriptionDto;
import com.cloud.usersservice.dto.user.*;
import com.cloud.usersservice.entity.User;
import com.cloud.usersservice.repository.UsersRepository;
import com.cloud.usersservice.service.UsersService;
import com.cloud.usersservice.dto.misc.NotificationMessageDto;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class UsersServiceImpl implements UsersService {
    private final UsersRepository repository;
    private final Logger logger = LoggerFactory.getLogger(UsersServiceImpl.class);
    private final ModelMapper mapper;
    private final RabbitTemplate rabbitTemplate;
    @Value("${notifications.exchange.name}")
    private String exchangeName;
    public UsersServiceImpl(UsersRepository repository, ModelMapper mapper, RabbitTemplate rabbitTemplate) {
        this.repository = Objects.requireNonNull(repository);
        this.mapper = mapper;
        this.rabbitTemplate = rabbitTemplate;
    }
    @Transactional(readOnly = true)
    public Collection<UserViewDto> getAll(){
        logger.info("Fetching all users...");
        return repository.findBy(UserViewDto.class);
    }
    @Transactional(readOnly = true)
    public Page<UserViewDto> getAll(int page, int pageCount){
        if(page <= 0 || pageCount <= 0) throw new IllegalArgumentException("Number of items per page is invalid");
        page--;
        Pageable pageable = PageRequest.of(page, pageCount).withSort(Sort.by("email").ascending());
        logger.info(String.format("Fetching %d users by page %d...", pageCount, page));
        return repository.findBy(UserViewDto.class, pageable);
    }
    @Transactional(readOnly = true)
    public Optional<UserFullViewDto> getById(UUID id) throws EntityNotFoundException {
        if(id == null) throw new IllegalArgumentException("Passed user id is invalid");
        logger.info("Fetching user by id " + id);
        return repository.findById(id, UserFullViewDto.class);
    }
    @Transactional(readOnly = true)
    public boolean existsById(UUID id){
        if(id == null) throw new IllegalArgumentException("Passed user id is invalid");
        return repository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public int getKarmaById(UUID id) {
        var result = repository.findKarmaById(id);
        if(result == null) throw new EntityNotFoundException("User with id " + id + " not found");
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<UserViewDto> getByIds(Collection<UUID> ids) {
        if(ids.isEmpty()) return Collections.emptyList();
        return repository.findByIdIsIn(ids);
    }

    @Transactional(readOnly = true)
    public Optional<UserWithRolesDto> getRolesByUsername(String username){
        if(!StringUtils.hasLength(username)) throw new IllegalArgumentException("Username is empty or invalid");
        logger.info("Fetching user roles by username " + username);
        return repository.findByUserName(username);
    }
    @Transactional
    public UserCreatedDto create(UserCreateDto userDto) throws EntityExistsException{
        Objects.requireNonNull(userDto);
        if(repository.existsByEmailOrUserNameAllIgnoreCase(userDto.getEmail(), userDto.getUserName()))
            throw new EntityExistsException("User with such credentials already exists");
        var user = mapper.map(userDto, User.class);
        user.setKarma(1);
        user.setRoles(List.of("user"));
        user = repository.save(user);
        return mapper.map(user, UserCreatedDto.class);
    }
    @Transactional
    public void update(UserUpdateDto dto) throws EntityNotFoundException{
        if(dto.getId() == null) throw new IllegalArgumentException("Passed user id is invalid");
        repository.findById(dto.getId()).ifPresentOrElse(user -> {
            if(StringUtils.hasLength(dto.getUserName()) && !dto.getUserName().equals(user.getUserName()))
                user.setUserName(dto.getUserName());
            if(StringUtils.hasLength(dto.getEmail()) && !dto.getEmail().equals(user.getEmail()))
                user.setEmail(dto.getEmail());
            logger.info("Updating user by id " + dto.getId());
            repository.save(user);
        }, () -> {
            throw new EntityNotFoundException(String.format("User with id %s not found", dto.getId()));
        });
    }
    @Transactional
    public void deleteById(UUID id) throws EntityNotFoundException {
        if(id == null) throw new IllegalArgumentException("Passed user id is invalid");
        if(!repository.existsById(id)) throw new EntityNotFoundException("User with id " + id + " doesn't exist");
        logger.info("Deleting user by id " + id);
        repository.deleteById(id);
    }
    @Transactional
    public void subscribe(SubscriptionDto subscriptionDto){
        Objects.requireNonNull(subscriptionDto);
        if(!StringUtils.hasLength(subscriptionDto.getUserId()) || !StringUtils.hasLength(subscriptionDto.getSubscriberId()))
            throw new IllegalArgumentException("User or subscriber ids are invalid");

        User user = repository.findById(UUID.fromString(subscriptionDto.getUserId()))
                .orElseThrow(() -> new EntityNotFoundException("User with id " + subscriptionDto.getUserId() + " not found"));
        User subscriber = repository.findById(UUID.fromString(subscriptionDto.getSubscriberId()))
                .orElseThrow(() -> new EntityNotFoundException("User with id " + subscriptionDto.getSubscriberId() + " not found"));

        logger.info("Subscription process: fetching user " + subscriptionDto.getUserId()
                + " and user-subscriber " + subscriptionDto.getSubscriberId());

        if(!user.getSubscribers().contains(subscriber))
            user.getSubscribers().add(subscriber);
        else
            throw new EntityExistsException("Subscription already exists");

        if(!subscriber.getSubscriberOf().contains(user))
            subscriber.getSubscriberOf().add(user);
        else
            throw new EntityExistsException("Subscription already exists");

        user.setKarma(user.getKarma() + 1);
        logger.info("Incrementing user's karma...");
        repository.saveAll(List.of(user, subscriber));
        logger.info(String.format("Subscription of user %s to user %s successful", subscriptionDto.getUserId(), subscriptionDto.getSubscriberId()));
        rabbitTemplate.convertAndSend(exchangeName, new NotificationMessageDto(
                subscriptionDto.getUserId(), subscriber.getUserName() + " has subscribed to you"));
    }
}
