package com.cloud.communitiesservice.service;

import com.cloud.communitiesservice.client.UsersClient;
import com.cloud.communitiesservice.dto.community.*;
import com.cloud.communitiesservice.dto.member.MembersListDto;
import com.cloud.communitiesservice.entity.Community;
import com.cloud.communitiesservice.entity.CommunityMember;
import com.cloud.communitiesservice.entity.CommunityMemberRole;
import com.cloud.communitiesservice.entity.RoleType;
import com.cloud.communitiesservice.entity.id.CommunityMemberId;
import com.cloud.communitiesservice.exception.UserNotFoundException;
import com.cloud.communitiesservice.repository.CommunitiesMembersRepository;
import com.cloud.communitiesservice.repository.CommunitiesRepository;
import com.cloud.communitiesservice.repository.CommunityRolesRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommunitiesServiceImpl implements CommunitiesService {
    private final CommunitiesRepository repository;
    private final CommunitiesMembersRepository membersRepository;
    private final CommunityRolesRepository rolesRepository;
    private final UsersClient client;
    private final ModelMapper mapper;
    public CommunitiesServiceImpl(CommunitiesRepository repository, CommunitiesMembersRepository membersRepository, CommunityRolesRepository rolesRepository, UsersClient client, ModelMapper mapper) {
        this.repository = repository;
        this.membersRepository = membersRepository;
        this.rolesRepository = rolesRepository;
        this.client = client;
        this.mapper = mapper;
        this.mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }
    @Override
    public Collection<CommunityViewDto> getAll() {
        return repository.findBy(CommunityViewDto.class);
    }
    @Override
    public Page<CommunityViewDto> getAll(int page, int pageCount) {
        if(page <= 0 || pageCount <= 0) throw new IllegalArgumentException("Number of items per page is invalid");
        page--;
        Pageable pageable = PageRequest.of(page, pageCount).withSort(Sort.by("name").ascending());
        return repository.findBy(CommunityViewDto.class, pageable);
    }
    @Override
    public Collection<CommunityViewDto> getAllByTag(String tag) {
        if(!StringUtils.hasLength(tag)) return Collections.emptyList();
        return repository.findAllByTagContainingIgnoreCase(tag);
    }
    @Override
    public Page<CommunityViewDto> getAllByTag(String tag, int page, int pageCount) {
        if(!StringUtils.hasLength(tag) || page <= 0 || pageCount <= 0) return Page.empty();
        page--;
        Pageable pageable = PageRequest.of(page, pageCount).withSort(Sort.by("name").ascending());
        return repository.findAllByTagContainingIgnoreCase(tag, pageable);
    }
    @Override
    public Collection<CommunityViewDto> getAllByName(String name) {
        if(!StringUtils.hasLength(name)) return Collections.emptyList();
        return repository.findAllByNameContaining(name);
    }
    @Override
    public Page<CommunityViewDto> getAllByName(String name, int page, int pageCount) {
        if(!StringUtils.hasLength(name) || page <= 0 || pageCount <= 0) return Page.empty();
        page--;
        Pageable pageable = PageRequest.of(page, pageCount).withSort(Sort.by("name").ascending());
        return repository.findAllByNameContaining(name, pageable);
    }
    @Override
    public void addCategory(long id, String category) {
        if(id <= 0 || !StringUtils.hasLength(category)) throw new IllegalArgumentException("Id or category name is invalid");
        repository.findById(id).ifPresentOrElse(community -> {
            community.getCategories().add(category);
            repository.save(community);
        }, () -> {throw new EntityNotFoundException("Community with id " + id + " is not found");});
    }

    @Override
    public void addMember(RoleType roleType, UUID memberId, long communityId) {
        Objects.requireNonNull(memberId);
        if(!client.userExists(memberId)) throw new UserNotFoundException("User with id " + memberId + " not found");
        var newMember = new CommunityMember(memberId, communityId);
        List<CommunityMemberRole> assignedRoles = new ArrayList<>();
        assignedRoles.add(rolesRepository.findByName(RoleType.USER));
        if(roleType != RoleType.USER) assignedRoles.add(rolesRepository.findByName(roleType));
        newMember.setRoles(assignedRoles);
        membersRepository.save(newMember);
    }

    @Override
    public Optional<CommunityFullViewWrapperDto> getById(Long id) {
        if(id <= 0) return Optional.empty();
        var community = repository.findById(id, CommunityWithCategoriesViewDto.class);
        if(community.isEmpty()) return Optional.empty();
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

        Optional<MembersListDto> adminsList = client.getByIds(adminIds), moderatorsList = client.getByIds(moderatorIds),
                topFiveUsersList = client.getByIds(userIds);
        communityView.setAdmins(adminsList.isPresent() ? adminsList.get().getUsers() : List.of());
        communityView.setModerators(moderatorsList.isPresent() ? moderatorsList.get().getUsers() : List.of());
        communityView.setMembers(topFiveUsersList.isPresent() ? topFiveUsersList.get().getUsers() : List.of());
        communityView.setMembersCount(membersRepository.count());
        return Optional.of(communityView);
    }
    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    public CommunityCreatedDto create(CommunityCreateDto dto) throws EntityExistsException, EntityNotFoundException {
        Objects.requireNonNull(dto);
        UUID ownerId;
        try {
            ownerId = UUID.fromString(dto.getOwnerId());
        } catch (Exception e){
            throw new IllegalArgumentException("Owner id is invalid");
        }
        if(repository.existsByTagOrName(dto.getTag(), dto.getName()))
            throw new EntityExistsException("Community with such tag or name already exists");

        if(!client.userExists(ownerId)) throw new UserNotFoundException("User with id " + dto.getOwnerId() + " not found");
        Community community = mapper.map(dto, Community.class);
        community = repository.save(community);
        var owner = new CommunityMember();
        owner.setRoles(List.of(rolesRepository.findByName(RoleType.SUPER_ADMIN), rolesRepository.findByName(RoleType.USER)));
        owner.setId(new CommunityMemberId(ownerId, community.getId()));
        owner.setCommunity(community);
        membersRepository.save(owner);
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
        repository.save(community);
    }

    @Override
    public void deleteById(Long id) throws EntityNotFoundException {
        if(id <= 0) throw new EntityNotFoundException("Community with id " + id + " is already deleted");
        repository.deleteById(id);
    }
}
