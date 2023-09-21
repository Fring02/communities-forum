package com.cloud.communitiesservice.service.impl;

import com.cloud.communitiesservice.client.UsersClient;
import com.cloud.communitiesservice.dto.category.CommunityCategoriesDto;
import com.cloud.communitiesservice.dto.community.*;
import com.cloud.communitiesservice.dto.member.MemberRolesDto;
import com.cloud.communitiesservice.dto.member.MembersListDto;
import com.cloud.communitiesservice.dto.member.NewMemberDto;
import com.cloud.communitiesservice.entity.Community;
import com.cloud.communitiesservice.entity.CommunityMember;
import com.cloud.communitiesservice.entity.CommunityMemberRole;
import com.cloud.communitiesservice.entity.RoleType;
import com.cloud.communitiesservice.entity.id.CommunityMemberId;
import com.cloud.communitiesservice.exception.UserNotFoundException;
import com.cloud.communitiesservice.repository.CommunitiesMembersRepository;
import com.cloud.communitiesservice.repository.CommunitiesRepository;
import com.cloud.communitiesservice.repository.CommunityRolesRepository;
import com.cloud.communitiesservice.service.CommunitiesService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommunitiesServiceImpl implements CommunitiesService {
    private final CommunitiesRepository repository;
    private final CommunitiesMembersRepository membersRepository;
    private final CommunityRolesRepository rolesRepository;
    private final UsersClient usersClient;
    private final ModelMapper mapper;
    private final Logger logger = LoggerFactory.getLogger(CommunitiesServiceImpl.class);
    public CommunitiesServiceImpl(CommunitiesRepository repository, CommunitiesMembersRepository membersRepository,
                                  CommunityRolesRepository rolesRepository, UsersClient usersClient, ModelMapper mapper) {
        this.repository = repository;
        this.membersRepository = membersRepository;
        this.rolesRepository = rolesRepository;
        this.usersClient = usersClient;
        this.mapper = mapper;
        this.mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }
    @Override
    public Collection<CommunityViewDto> getAll() {
        logger.info("Fetching all communities...");
        return repository.findBy(CommunityViewDto.class);
    }
    @Override
    public Page<CommunityViewDto> getAll(int page, int pageCount) {
        if(page <= 0 || pageCount <= 0) throw new IllegalArgumentException("Number of items per page is invalid");
        page--;
        Pageable pageable = PageRequest.of(page, pageCount).withSort(Sort.by("name").ascending());
        logger.info("Fetching communities with paging on: " + pageCount + " communities per page №" + page);
        return repository.findBy(CommunityViewDto.class, pageable);
    }
    @Override
    public Collection<CommunityViewDto> getAllByTag(String tag) {
        if(!StringUtils.hasLength(tag)) return Collections.emptyList();
        logger.info("Fetching communities by tag " + tag);
        return repository.findAllByTagContainingIgnoreCase(tag);
    }
    @Override
    public Page<CommunityViewDto> getAllByTag(String tag, int page, int pageCount) {
        if(!StringUtils.hasLength(tag) || page <= 0 || pageCount <= 0) return Page.empty();
        page--;
        Pageable pageable = PageRequest.of(page, pageCount).withSort(Sort.by("name").ascending());
        logger.info("Fetching communities by tag " + tag + " with paging on: " + pageCount + " communities per page №" + page);
        return repository.findAllByTagContainingIgnoreCase(tag, pageable);
    }
    @Override
    public Collection<CommunityViewDto> getAllByName(String name) {
        if(!StringUtils.hasLength(name)) return Collections.emptyList();
        logger.info("Fetching communities by name " + name);
        return repository.findAllByNameContaining(name);
    }
    @Override
    public Page<CommunityViewDto> getAllByName(String name, int page, int pageCount) {
        if(!StringUtils.hasLength(name) || page <= 0 || pageCount <= 0) return Page.empty();
        page--;
        Pageable pageable = PageRequest.of(page, pageCount).withSort(Sort.by("name").ascending());
        logger.info("Fetching communities by name " + name + " with paging on: " + pageCount + " communities per page №" + page);
        return repository.findAllByNameContaining(name, pageable);
    }
    @Override
    public void addCategory(long id, String category) {
        if(id <= 0 || !StringUtils.hasLength(category)) throw new IllegalArgumentException("Id or category name is invalid");
        repository.findById(id).ifPresentOrElse(community -> {
            community.getCategories().add(category);
            repository.save(community);
            logger.info("Added category " + category + " to community " + community.getName());
        }, () -> {throw new EntityNotFoundException("Community with id " + id + " is not found");});
    }

    @Override
    public Optional<CommunityCategoriesDto> getCategoriesByCommunityId(long communityId) {
        logger.info("Fetching categories of community " + communityId);
        return repository.findById(communityId, CommunityCategoriesDto.class);
    }

    @Override
    public void addMember(NewMemberDto memberDto, long communityId) {
        if(!usersClient.userExists(memberDto.getUserId())) throw new UserNotFoundException("User with id " + memberDto.getUserId() + " not found");
        var newMember = new CommunityMember(memberDto.getUserId(), communityId);
        newMember.setUsername(memberDto.getUsername());
        RoleType roleType = Enum.valueOf(RoleType.class, memberDto.getRole());
        List<CommunityMemberRole> assignedRoles = new ArrayList<>();
        assignedRoles.add(rolesRepository.findByName(RoleType.USER));
        if(roleType != RoleType.USER) assignedRoles.add(rolesRepository.findByName(roleType));
        newMember.setRoles(assignedRoles);
        membersRepository.save(newMember);
        logger.info("New member " + memberDto.getUserId() + " with assigned roles " + assignedRoles + " added");
    }

    @Override
    public Optional<CommunityKarmaDto> getKarmaById(long id) {
        logger.info("Fetching community's karma with id " + id + "...");
        return repository.findById(id, CommunityKarmaDto.class);
    }

    @Override
    public Optional<MemberRolesDto> getRolesByUsername(long communityId, String username) {
        if(communityId <= 0 || !StringUtils.hasLength(username)) return Optional.empty();
        return membersRepository.findByUsernameAndId_CommunityId(username, communityId);
    }

    @Override
    public Optional<CommunityFullViewWrapperDto> getById(Long id) {
        if(id <= 0) return Optional.empty();
        var community = repository.findById(id, CommunityWithCategoriesViewDto.class);
        if(community.isEmpty()) return Optional.empty();
        logger.info("Found community with id " + id);
        var communityView = new CommunityFullViewWrapperDto(community.get());
        var roles = rolesRepository.findAll();
        Collection<UUID> adminIds = membersRepository.findByRolesIn(roles.stream().filter(r ->
                r.getName().equals(RoleType.SUPER_ADMIN) || r.getName().equals(RoleType.ADMIN)).toList())
                .stream().map(d -> d.getId().getUserId()).collect(Collectors.toList()),

                moderatorIds = membersRepository.findByRolesIn(roles.stream().filter(r ->
                r.getName().equals(RoleType.MODERATOR)).toList())
                .stream().map(d -> d.getId().getUserId()).collect(Collectors.toList()),

                userIds = membersRepository.findByRolesIn(roles.stream().filter(r ->
                r.getName().equals(RoleType.USER)).toList(), Pageable.ofSize(5))
                .stream().map(d -> d.getId().getUserId()).collect(Collectors.toList());

        logger.info("Fetched ids for admin users, moderator users, member users...");
        Optional<MembersListDto> adminsList = usersClient.getByIds(adminIds), moderatorsList = usersClient.getByIds(moderatorIds),
                topFiveUsersList = usersClient.getByIds(userIds);
        communityView.setAdmins(adminsList.isPresent() ? adminsList.get().getUsers() : List.of());
        communityView.setModerators(moderatorsList.isPresent() ? moderatorsList.get().getUsers() : List.of());
        communityView.setMembers(topFiveUsersList.isPresent() ? topFiveUsersList.get().getUsers() : List.of());
        communityView.setMembersCount(membersRepository.count());
        logger.info("Fetched community by id " + id + " with all details");
        return Optional.of(communityView);
    }
    @Override
    public boolean existsById(Long id) {
        logger.info("Fetching if community exists by id " + id);
        return repository.existsById(id);
    }

    @Override
    public CommunityCreatedDto create(CommunityCreateDto dto) throws EntityExistsException, EntityNotFoundException {
        Objects.requireNonNull(dto);
        if(repository.existsByTagOrName(dto.getTag(), dto.getName()))
            throw new EntityExistsException("Community with such tag or name already exists");
        if(!usersClient.userExists(dto.getOwnerId())) throw new UserNotFoundException("User with id " + dto.getOwnerId() + " not found");
        Community community = mapper.map(dto, Community.class);
        community = repository.save(community);
        var owner = new CommunityMember();
        owner.setRoles(List.of(rolesRepository.findByName(RoleType.ADMIN), rolesRepository.findByName(RoleType.USER)));
        owner.setId(new CommunityMemberId(dto.getOwnerId(), community.getId()));
        owner.setCommunity(community);
        owner.setUsername(dto.getOwnerUsername());
        membersRepository.save(owner);
        logger.info("Created community " + dto.getName() + " with tag: " + dto.getTag());
        logger.info("Made user " + dto.getOwnerUsername() + " as an owner for community " + dto.getName());
        var createdCommunity = mapper.map(community, CommunityCreatedDto.class);
        createdCommunity.setOwnerId(dto.getOwnerId());
        createdCommunity.setOwnerUsername(dto.getOwnerUsername());
        return createdCommunity;
    }

    @Override
    public void update(CommunityUpdateDto dto) throws EntityNotFoundException {
        Objects.requireNonNull(dto);
        var communityOpt = repository.findById(dto.getId());
        if(communityOpt.isEmpty()) throw new EntityNotFoundException("Community with id " + dto.getId() + " not found");
        var community = communityOpt.get();
        if(StringUtils.hasLength(dto.getName()) && !community.getName().equals(dto.getName()))
            community.setName(dto.getName());
        if(StringUtils.hasLength(dto.getTag()) && !community.getTag().equals(dto.getTag()))
            community.setTag(dto.getTag());
        if(StringUtils.hasLength(dto.getDescription()) && !community.getDescription().equals(dto.getDescription()))
            community.setDescription(dto.getDescription());
        if(dto.getRequiredKarma() > 0 && community.getRequiredKarma() != dto.getRequiredKarma())
            community.setRequiredKarma(dto.getRequiredKarma());
        logger.info("Updating community " + dto.getId() + "...");
        repository.save(community);
    }

    @Override
    public void deleteById(Long id) throws EntityNotFoundException {
        if(id <= 0) throw new EntityNotFoundException("Community with id " + id + " is already deleted");
        logger.info("Deleting community with id " + id);
        repository.deleteById(id);
    }
}
