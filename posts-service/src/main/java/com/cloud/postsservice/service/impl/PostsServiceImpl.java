package com.cloud.postsservice.service.impl;

import com.cloud.postsservice.client.CommunitiesClient;
import com.cloud.postsservice.client.UsersClient;
import com.cloud.postsservice.dto.post.*;
import com.cloud.postsservice.entity.*;
import com.cloud.postsservice.entity.id.DislikeId;
import com.cloud.postsservice.entity.id.LikeId;
import com.cloud.postsservice.entity.id.ViewId;
import com.cloud.postsservice.exception.ResourceNotFoundException;
import com.cloud.postsservice.repository.*;
import com.cloud.postsservice.service.PostsService;
import com.cloud.postsservice.util.NotificationMessageDto;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang.StringUtils;

import java.time.LocalDate;
import java.util.*;

@Service
@RefreshScope
public class PostsServiceImpl implements PostsService {
    private final PostsRepository repository;
    private final LikesRepository likesRepository;
    private final DislikesRepository dislikesRepository;
    private final ModelMapper mapper;
    private final UsersClient usersClient;
    private final CommunitiesClient communitiesClient;
    private final ViewsRepository viewsRepository;
    private final RabbitTemplate rabbitTemplate;
    private final Logger logger = LoggerFactory.getLogger(PostsServiceImpl.class);
    @Value("${notifications.exchange.name}")
    private String exchangeName;

    public PostsServiceImpl(PostsRepository repository, LikesRepository likesRepository,
                            DislikesRepository dislikesRepository, ModelMapper mapper, UsersClient usersClient,
                            CommunitiesClient communitiesClient, ViewsRepository viewsRepository, RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.likesRepository = likesRepository;
        this.dislikesRepository = dislikesRepository;
        this.mapper = mapper;
        this.communitiesClient = communitiesClient;
        this.rabbitTemplate = rabbitTemplate;
        this.mapper.addMappings(new PropertyMap<PostCreateDto, Post>() {
            @Override
            protected void configure() {
                skip(destination.getId());
            }
        });
        this.usersClient = usersClient;
        this.viewsRepository = viewsRepository;
    }
    @Transactional
    public PostCreatedDto create(PostCreateDto dto) throws EntityExistsException, EntityNotFoundException {
        Objects.requireNonNull(dto);
        if(repository.existsByTitle(dto.getTitle()))
            throw new EntityExistsException("Post with such title already exists");
        if(!communitiesClient.communityExists(dto.getCommunityId()))
            throw new ResourceNotFoundException("Community with id " + dto.getCommunityId() + " not found");
        if(!usersClient.userExists(dto.getOwnerId()))
            throw new ResourceNotFoundException("User with id " + dto.getOwnerId() + " not found");
        var userKarmaResponse = usersClient.getUserKarma(dto.getOwnerId());
        logger.info("Fetched user's karma...");
        if(!userKarmaResponse.getStatusCode().is2xxSuccessful()) throw new IllegalArgumentException("User fetch error");
        long requiredKarma = communitiesClient.getById(dto.getCommunityId(), true);
        logger.info("Fetched community's karma: " + requiredKarma);
        if(userKarmaResponse.getBody() < requiredKarma)
            throw new IllegalArgumentException("User with id " + dto.getOwnerId() + " not found");

        Set<String> categories = communitiesClient.getCommunityCategories(dto.getCommunityId());
        if(categories.isEmpty() || !categories.contains(dto.getCategory()))
            throw new IllegalArgumentException("Category with name " + dto.getCategory() + " is not found in this community");
        Post post = mapper.map(dto, Post.class);
        post.setOwnerId(dto.getOwnerId());
        post.setPostedAt(LocalDate.now());
        post = repository.save(post);
        logger.info("Added post to community " + dto.getCommunityId());
        //adding 1 view as post owner's view
        viewsRepository.save(new View(dto.getOwnerId(), post.getId(), post));
        logger.info("Created post has 1 view by default");
        var newPost = mapper.map(post, PostCreatedDto.class);
        newPost.setCategory(dto.getCategory());
        newPost.setViewCount(1);
        rabbitTemplate.convertAndSend(exchangeName, new NotificationMessageDto(dto.getOwnerId().toString(), "A new post was created"));
        logger.info("Notification message sent to notifications queue.");
        return newPost;
    }
    @Transactional(readOnly = true)
    public Collection<PostViewDto> getAll(){
        logger.info("Fetching all posts...");
        return repository.findBy(PostViewDto.class);
    }
    @Transactional(readOnly = true)
    public Page<PostViewDto> getAll(int page, int pageCount){
        if(page <= 0 || pageCount <= 0) throw new IllegalArgumentException("Number of items per page is invalid");
        page--;
        Pageable pageable = PageRequest.of(page, pageCount).withSort(Sort.by("postedAt").ascending());
        logger.info("Fetching " + pageCount + " posts by page " + page);
        return repository.findBy(PostViewDto.class, pageable);
    }
    @Transactional(readOnly = true)
    public Optional<PostFullViewDto> getById(Long id) {
        if(id <= 0) return Optional.empty();
        logger.info("Fetching post by id " + id);
        return repository.findById(id, PostFullViewDto.class);
    }
    @Transactional(readOnly = true)
    @Override
    public boolean existsById(Long id) {
        if(id <= 0) return false;
        logger.info("Fetching if post exists by id " + id);
        return repository.existsById(id);
    }
    @Transactional
    public void update(PostUpdateDto dto) throws EntityNotFoundException {
        Objects.requireNonNull(dto);
        var postOpt = repository.findById(dto.getId());
        if(postOpt.isEmpty()) throw new EntityNotFoundException("Post with id " + dto.getId() + " not found");
        var post = postOpt.get();
        if(!StringUtils.isBlank(dto.getTitle()) && !dto.getTitle().equals(post.getTitle()))
            post.setTitle(dto.getTitle());
        if(!StringUtils.isBlank(dto.getDescription()) && !dto.getDescription().equals(post.getDescription()))
            post.setDescription(dto.getDescription());
        if(!StringUtils.isBlank(dto.getCategory()) && !dto.getCategory().equals(post.getCategory())){
            Set<String> categories = communitiesClient.getCommunityCategories(post.getCommunityId());
            if(categories.isEmpty() || !categories.contains(dto.getCategory()))
                throw new IllegalArgumentException("Category with name " + dto.getCategory() + " is not found in this community");
            post.setCategory(dto.getCategory());
        }
        logger.info("Updating post by id " + dto.getId());
        repository.save(post);
    }
    @Transactional
    public void deleteById(Long id) throws EntityNotFoundException {
        if(id <= 0) throw new IllegalArgumentException("ID is invalid");
        if(!repository.existsById(id)) throw new EntityNotFoundException("Post already deleted");
        logger.info("Deleted post with id " + id);
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostViewDto> getAll(long communityId, String category, int page, int pageCount) {
        if(page <= 0 || pageCount <= 0) throw new IllegalArgumentException("Number of items per page is invalid");
        page--;
        Pageable pageable = PageRequest.of(page, pageCount).withSort(Sort.by("postedAt").ascending());
        if(communityId > 0 && !StringUtils.isBlank(category)){
            logger.info("Fetching posts with paging, community and category filter...");
            return repository.findByCommunityIdAndCategory(communityId, category, pageable);
        }
        else {
            logger.info("Fetching posts with paging and community filter only...");
            return repository.findByCommunityId(communityId, pageable);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<PostViewDto> getAll(long communityId, String category) {
        if(communityId > 0 && !StringUtils.isBlank(category)) {
            logger.info("Fetching posts with community and category filter...");
            return repository.findByCommunityIdAndCategory(communityId, category);
        }
        else {
            logger.info("Fetching posts with community filter only...");
            return repository.findByCommunityId(communityId);
        }
    }

    @Override
    @Transactional
    public long updateViewsCount(long postId, UUID userId) throws EntityNotFoundException {
        if(postId <= 0) throw new IllegalArgumentException("Post id is invalid");
        Objects.requireNonNull(userId);
        var postOpt = repository.findById(postId);
        if(postOpt.isEmpty()) throw new EntityNotFoundException("Post with id " + postId + " doesn't exist");
        var post = postOpt.get();
        var viewId = new ViewId(userId, post.getId());
        if(!viewsRepository.existsById(viewId)) viewsRepository.save(new View(userId, post.getId(), post));
        logger.info("Incrementing post's views with id " + postId);
        return viewsRepository.countById_PostId(post.getId());
    }
    @Override
    @Transactional
    public void updateLikesCount(long postId, UUID userId) throws EntityNotFoundException {
        if(postId <= 0) throw new IllegalArgumentException("Post id is invalid");
        Objects.requireNonNull(userId);
        var postOpt = repository.findById(postId);
        if(postOpt.isEmpty()) throw new EntityNotFoundException("Post with id " + postId + " doesn't exist");
        var post = postOpt.get();
        var likeId = new LikeId(userId, post.getId());
        if(!likesRepository.existsById(likeId)) likesRepository.save(new Like(userId, post.getId(), post));
        var dislikeId = new DislikeId(post.getId(), userId);
        if(dislikesRepository.existsById(dislikeId)) dislikesRepository.deleteById(dislikeId);
        logger.info("Incrementing post's likes with id " + postId);
    }
    @Override
    @Transactional
    public void updateDislikesCount(long postId, UUID userId) throws EntityNotFoundException {
        if(postId <= 0) throw new IllegalArgumentException("Post id is invalid");
        Objects.requireNonNull(userId);
        var postOpt = repository.findById(postId);
        if(postOpt.isEmpty()) throw new EntityNotFoundException("Post with id " + postId + " doesn't exist");
        var post = postOpt.get();
        var dislikeId = new DislikeId(post.getId(), userId);
        if(!dislikesRepository.existsById(dislikeId)) dislikesRepository.save(new Dislike(userId, post.getId(), post));
        var likeId = new LikeId(userId, post.getId());
        if(likesRepository.existsById(likeId)) likesRepository.deleteById(likeId);
        logger.info("Incrementing post's dislikes with id " + postId);
    }
}
