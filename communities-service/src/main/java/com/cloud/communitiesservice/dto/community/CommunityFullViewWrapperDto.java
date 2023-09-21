package com.cloud.communitiesservice.dto.community;

import com.cloud.communitiesservice.dto.member.MemberDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class CommunityFullViewWrapperDto implements CommunityFullViewDto {
    private long id;
    private String tag;
    private String name;
    private String description;
    private long requiredKarma;
    private long membersCount;
    private Set<String> categories;
    private Collection<MemberDto> members;
    private Collection<MemberDto> admins;
    private Collection<MemberDto> moderators;
    public Long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public CommunityFullViewWrapperDto(CommunityWithCategoriesViewDto dto){
        this.id = dto.getId();
        this.requiredKarma = dto.getRequiredKarma();
        this.tag = dto.getTag();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.membersCount = dto.getMembersCount();
        this.categories = dto.getCategories();
    }
    @Override
    public long getModeratorsCount() {
        return moderators.size();
    }
    @Override
    public long getAdminsCount() {
        return admins.size();
    }
}
