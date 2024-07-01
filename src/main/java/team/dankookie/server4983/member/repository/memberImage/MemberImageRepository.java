package team.dankookie.server4983.member.repository.memberImage;

import org.springframework.data.jpa.repository.JpaRepository;
import team.dankookie.server4983.member.domain.Member;
import team.dankookie.server4983.member.domain.MemberImage;

public interface MemberImageRepository extends JpaRepository<MemberImage, Long>, MemberImageRepositoryCustom {


    MemberImage findByMember(Member member);

    long deleteByMember(Member member);

    long deleteMemberImageByMemberAndImageUrl(Member member, String imageUrl);
}
