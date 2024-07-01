package team.dankookie.server4983.book.repository.mypageBookPurchaseDetail;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import team.dankookie.server4983.book.constant.BookStatus;
import team.dankookie.server4983.book.dto.QUsedBookListResponse;
import team.dankookie.server4983.book.dto.UsedBookListResponse;

import java.util.List;
import java.util.Objects;

import static team.dankookie.server4983.book.domain.QBookImage.bookImage;
import static team.dankookie.server4983.book.domain.QUsedBook.usedBook;

@RequiredArgsConstructor
public class BookPurchaseDetailRepositoryImpl implements BookPurchaseDetailRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<UsedBookListResponse> getMyPageBookPurchaseDetailList(BookStatus bookStatus, Long memberId) {
        JPAQuery<UsedBookListResponse> query = queryFactory.select(
                new QUsedBookListResponse(
                        usedBook.id,
                        JPAExpressions.select(bookImage.imageUrl)
                                .from(bookImage)
                                .where(bookImage.usedBook.eq(usedBook))
                                .orderBy(bookImage.id.asc()),
                        usedBook.bookStatus,
                        usedBook.name,
                        usedBook.tradeAvailableDatetime,
                        usedBook.createdAt,
                        usedBook.price
                )
        ).from(usedBook);
        return getMyPageBookStatus(bookStatus, memberId, query);
    }

    private List<UsedBookListResponse> getMyPageBookStatus(BookStatus bookStatus, Long memberId, JPAQuery<UsedBookListResponse> query) {
        if (Objects.requireNonNull(bookStatus) == BookStatus.SOLD) {
            return query
                    .where(
                            usedBook.buyerMember.id.eq(memberId)
                    )
                    .orderBy(usedBook.createdAt.desc()).fetch();
        }
        return null;
    }
}