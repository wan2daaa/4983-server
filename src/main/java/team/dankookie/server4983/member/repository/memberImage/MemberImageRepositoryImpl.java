package team.dankookie.server4983.member.repository.memberImage;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static team.dankookie.server4983.member.domain.QMemberImage.memberImage;

@RequiredArgsConstructor
public class MemberImageRepositoryImpl implements MemberImageRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public String getMemberImageUrlByMemberId(Long memberId) {
        return queryFactory.select(memberImage.imageUrl)
                .from(memberImage)
                .where(memberImage.member.id.eq(memberId))
                .fetchOne();
    }
}
