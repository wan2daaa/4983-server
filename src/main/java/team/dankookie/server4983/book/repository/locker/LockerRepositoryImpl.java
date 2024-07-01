package team.dankookie.server4983.book.repository.locker;

import static team.dankookie.server4983.book.domain.QLocker.locker;
import static team.dankookie.server4983.chat.domain.QChatRoom.chatRoom;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.LocalDate;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import team.dankookie.server4983.book.dto.AdminLockerListResponse;
import team.dankookie.server4983.book.dto.LockerResponse;
import team.dankookie.server4983.book.dto.QAdminLockerListResponse;
import team.dankookie.server4983.book.dto.QLockerResponse;
import team.dankookie.server4983.chat.domain.QChatRoom;
import team.dankookie.server4983.member.domain.QMember;

@RequiredArgsConstructor
public class LockerRepositoryImpl implements LockerRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<LockerResponse> findByTradeDateBetweenAndIsExistsTrue(LocalDate startDate,
                                                                      LocalDate endDate) {
        return queryFactory.select(
                        new QLockerResponse(locker.lockerNumber, locker.isExists)
                ).from(locker)
                .where(
                        locker.tradeDate.between(startDate, endDate),
                        locker.isExists.isTrue()
                )
                .fetch();
    }

    @Override
    public List<AdminLockerListResponse> getAdminLockerList(String searchKeyword) {
        QMember seller = new QMember("seller");
        QMember buyer = new QMember("buyer");

        BooleanExpression isStudentIdContains =
                searchKeyword.equals("") ? null :
                        seller.studentId.contains(searchKeyword)
                                .or(buyer.studentId.contains(searchKeyword))
                                .or(locker.lockerNumber.eq(Integer.parseInt(searchKeyword)));

        return queryFactory
                .select(
                        new QAdminLockerListResponse(
                                locker.lockerNumber,
                                seller.studentId,
                                locker.isExists,
                                buyer.studentId,
                                locker.tradeDate
                        )
                ).from(locker)
                .leftJoin(chatRoom).on(chatRoom.eq(locker.chatRoom))
                .leftJoin(seller).on(seller.eq(chatRoom.seller))
                .leftJoin(buyer).on(buyer.eq(chatRoom.buyer))
                .where(
                        locker.isExists.eq(true),
                        isStudentIdContains
                )
                .orderBy(locker.lockerNumber.asc())
                .fetch();
    }

}
