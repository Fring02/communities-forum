package com.cloud.usersservice.service;

import com.cloud.usersservice.dto.subscribe.SubscriptionDto;
import com.cloud.usersservice.dto.user.*;
import com.cloud.usersservice.entity.User;
import com.cloud.usersservice.repository.UsersRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class UserService {
    private final UsersRepository repository;
    private final Logger logger;
    private final ModelMapper mapper;
    public UserService(UsersRepository repository, ModelMapper mapper) {
        this.repository = Objects.requireNonNull(repository);
        this.mapper = mapper;
        logger = LoggerFactory.getLogger(UserService.class);
    }
    @Transactional(readOnly = true)
    public List<UserViewDto> getAll(){
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
    public UserViewDto getById(UUID id){
        if(id == null) throw new IllegalArgumentException("Passed user id is invalid");
        logger.info("Fetching user by id " + id);
        return repository.findById(id, UserFullViewDto.class)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id %s not found", id)));
    }
    @Transactional(readOnly = true)
    public UserWithRolesDto getRolesByUsername(String username){
        if(!StringUtils.hasLength(username)) throw new IllegalArgumentException("Username is empty or invalid");
        logger.info("Fetching user roles by username " + username);
        return repository.findByUserName(username);
    }
    @Transactional
    public UserCreatedDto create(UserCreateDto userDto){
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
    public void update(UserUpdateDto dto){
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
    public void deleteById(UUID id){
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
    }
}
