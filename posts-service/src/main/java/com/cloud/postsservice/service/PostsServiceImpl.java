package com.cloud.postsservice.service;

import com.cloud.postsservice.dto.*;
import com.cloud.postsservice.entity.Dislike;
import com.cloud.postsservice.entity.Like;
import com.cloud.postsservice.entity.Post;
import com.cloud.postsservice.entity.View;
import com.cloud.postsservice.entity.id.DislikeId;
import com.cloud.postsservice.entity.id.LikeId;
import com.cloud.postsservice.entity.id.ViewId;
import com.cloud.postsservice.exception.EntityExistsException;
import com.cloud.postsservice.exception.EntityNotFoundException;
import com.cloud.postsservice.exception.UserNotFoundException;
import com.cloud.postsservice.repository.DislikesRepository;
import com.cloud.postsservice.repository.LikesRepository;
import com.cloud.postsservice.repository.PostsRepository;
import com.cloud.postsservice.repository.ViewsRepository;
import com.cloud.postsservice.util.UsersRestClient;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class PostsServiceImpl implements PostsService {
    private final PostsRepository repository;
    private final LikesRepository likesRepository;
    private final DislikesRepository dislikesRepository;
    private final ModelMapper mapper;
    private final UsersRestClient usersClient;
    private final ViewsRepository viewsRepository;

    public PostsServiceImpl(PostsRepository repository, LikesRepository likesRepository,
                            DislikesRepository dislikesRepository, ModelMapper mapper, UsersRestClient usersClient,
                            ViewsRepository viewsRepository) {
        this.repository = repository;
        this.likesRepository = likesRepository;
        this.dislikesRepository = dislikesRepository;
        this.mapper = mapper;
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
        UUID ownerId;
        try {
            ownerId = UUID.fromString(dto.getOwnerId());
        } catch (Exception e){
            throw new IllegalArgumentException("Owner id is invalid");
        }
        if(repository.existsByTitle(dto.getTitle())) throw new EntityExistsException("Post with such title already exists");
        if(!usersClient.userExists(ownerId)) throw new UserNotFoundException("User with id " + dto.getOwnerId() + " not found");
        Post post = mapper.map(dto, Post.class);
        post.setOwnerId(ownerId);
        post.setPostedAt(LocalDate.now());
        post = repository.save(post);
        //adding 1 view as post owner's view
        viewsRepository.save(new View(ownerId, post.getId(), post));
        return mapper.map(post, PostCreatedDto.class);
    }
    @Transactional(readOnly = true)
    public List<PostViewDto> getAll(){
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
        if(StringUtils.hasLength(dto.getTitle()) && !dto.getTitle().equals(post.getTitle()))
            post.setTitle(dto.getTitle());
        if(StringUtils.hasLength(dto.getDescription()) && !dto.getDescription().equals(post.getDescription()))
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
