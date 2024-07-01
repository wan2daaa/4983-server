package team.dankookie.server4983.member.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import team.dankookie.server4983.member.dto.AdminMemberListResponse;

public interface MemberRepositoryCustom {
    Page<AdminMemberListResponse> getMember(Pageable pageable, String searchKeyword);

    Page<AdminMemberListResponse> getBlockedMember(Pageable pageable, String searchKeyword);
}
