package com.cloud.postsservice.service;

import com.cloud.postsservice.client.CommunitiesClient;
import com.cloud.postsservice.client.UsersClient;
import com.cloud.postsservice.dto.*;
import com.cloud.postsservice.entity.*;
import com.cloud.postsservice.entity.id.DislikeId;
import com.cloud.postsservice.entity.id.LikeId;
import com.cloud.postsservice.entity.id.ViewId;
import com.cloud.postsservice.exception.ResourceNotFoundException;
import com.cloud.postsservice.repository.*;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Value;
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
public class PostsServiceImpl implements PostsService {
    private final PostsRepository repository;
    private final LikesRepository likesRepository;
    private final DislikesRepository dislikesRepository;
    private final CategoriesRepository categoriesRepository;
    private final ModelMapper mapper;
    private final UsersClient usersClient;
    private final CommunitiesClient communitiesClient;
    private final ViewsRepository viewsRepository;
    @Value("${posts.karma.min}")
    private int requiredKarma;

    public PostsServiceImpl(PostsRepository repository, LikesRepository likesRepository,
                            DislikesRepository dislikesRepository, CategoriesRepository categoriesRepository, ModelMapper mapper, UsersClient usersClient,
                            CommunitiesClient communitiesClient, ViewsRepository viewsRepository) {
        this.repository = repository;
        this.likesRepository = likesRepository;
        this.dislikesRepository = dislikesRepository;
        this.categoriesRepository = categoriesRepository;
        this.mapper = mapper;
        this.communitiesClient = communitiesClient;
        this.mapper.addMappings(new PropertyMap<PostCreateDto, Post>() {
            @Override
            protected void configure() {
                skip(destination.getId());
                skip(destination.getCategory());
            }
        });
        this.usersClient = usersClient;
        this.viewsRepository = viewsRepository;
    }
    @Transactional
    public PostCreatedDto create(PostCreateDto dto) throws EntityExistsException, EntityNotFoundException {
        Objects.requireNonNull(dto);
        UUID ownerId;
        try {
            ownerId = UUID.fromString(dto.getOwnerId());
        } catch (Exception e){
            throw new IllegalArgumentException("Owner id is invalid");
        }
        if(repository.existsByTitle(dto.getTitle()))
            throw new EntityExistsException("Post with such title already exists");
        if(!communitiesClient.communityExists(dto.getCommunityId()))
            throw new ResourceNotFoundException("Community with id " + dto.getCommunityId() + " not found");
        if(!usersClient.userExists(ownerId))
            throw new ResourceNotFoundException("User with id " + dto.getOwnerId() + " not found");
        var userHasEnoughKarmaResponse = usersClient.getUserRoles(ownerId);
        if(!userHasEnoughKarmaResponse.getStatusCode().is2xxSuccessful()) throw new IllegalArgumentException("User fetch error");
        if(userHasEnoughKarmaResponse.getBody() < requiredKarma)
            throw new IllegalArgumentException("User with id " + dto.getOwnerId() + " not found");

        Category categoryOpt = categoriesRepository.findByNameAndCommunityId(dto.getCategory(), dto.getCommunityId());
        if(categoryOpt == null)
            throw new EntityNotFoundException("Category with name " + dto.getCategory() + " is not found in this community");
        Post post = mapper.map(dto, Post.class);
        post.setOwnerId(ownerId);
        post.setPostedAt(LocalDate.now());
        post.setCategory(categoryOpt);
        post = repository.save(post);
        //adding 1 view as post owner's view
        viewsRepository.save(new View(ownerId, post.getId(), post));
        var newPost = mapper.map(post, PostCreatedDto.class);
        newPost.setCategory(categoryOpt.getName());
        newPost.setViewCount(1);
        return newPost;
    }
    @Transactional(readOnly = true)
    public Collection<PostViewDto> getAll(){
        return repository.findBy(PostViewDto.class);
    }
    @Transactional(readOnly = true)
    public Page<PostViewDto> getAll(int page, int pageCount){
        if(page <= 0 || pageCount <= 0) throw new IllegalArgumentException("Number of items per page is invalid");
        page--;
        Pageable pageable = PageRequest.of(page, pageCount).withSort(Sort.by("postedAt").ascending());
        return repository.findBy(PostViewDto.class, pageable);
    }
    @Transactional(readOnly = true)
    public Optional<PostFullViewDto> getById(Long id) {
        if(id <= 0) return Optional.empty();
        return repository.findById(id, PostFullViewDto.class);
    }
    @Transactional(readOnly = true)
    @Override
    public boolean existsById(Long id) {
        if(id <= 0) return false;
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
        repository.save(post);
    }
    @Transactional
    public void deleteById(Long id) throws EntityNotFoundException {
        if(id <= 0) throw new IllegalArgumentException("ID is invalid");
        if(!repository.existsById(id)) throw new EntityNotFoundException("Post already deleted");
        repository.deleteById(id);
    }

    @Override
    public Page<PostViewDto> getAll(long communityId, String category, int page, int pageCount) {
        if(page <= 0 || pageCount <= 0) throw new IllegalArgumentException("Number of items per page is invalid");
        page--;
        Pageable pageable = PageRequest.of(page, pageCount).withSort(Sort.by("postedAt").ascending());
        if(communityId > 0 && !StringUtils.isBlank(category))
            return repository.findByCommunityIdAndCategory_Name(communityId, category, pageable);
        else
            return repository.findByCommunityId(communityId, pageable);
    }

    @Override
    public Collection<PostViewDto> getAll(long communityId, String category) {
        if(communityId > 0 && !StringUtils.isBlank(category))
            return repository.findByCommunityIdAndCategory_Name(communityId, category);
        else
            return repository.findByCommunityId(communityId);
    }

    @Override
    public long updateViewsCount(long postId, UUID userId) throws EntityNotFoundException {
        if(postId <= 0) throw new IllegalArgumentException("Post id is invalid");
        Objects.requireNonNull(userId);
        var postOpt = repository.findById(postId);
        if(postOpt.isEmpty()) throw new EntityNotFoundException("Post with id " + postId + " doesn't exist");
        var post = postOpt.get();
        var viewId = new ViewId(userId, post.getId());
        if(!viewsRepository.existsById(viewId)) viewsRepository.save(new View(userId, post.getId(), post));
        return viewsRepository.countById_PostId(post.getId());
    }
    @Override
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
    }
    @Override
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
    }
}
