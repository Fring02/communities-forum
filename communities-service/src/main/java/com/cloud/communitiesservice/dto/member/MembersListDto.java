package com.cloud.communitiesservice.dto.member;

import com.cloud.communitiesservice.dto.member.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MembersListDto {
    private Collection<MemberDto> users;
    private long count;
}
