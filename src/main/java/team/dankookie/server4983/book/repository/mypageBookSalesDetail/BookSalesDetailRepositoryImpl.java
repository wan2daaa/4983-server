package team.dankookie.server4983.book.repository.mypageBookSalesDetail;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import team.dankookie.server4983.book.constant.BookStatus;
import team.dankookie.server4983.book.dto.QUsedBookListResponse;
import team.dankookie.server4983.book.dto.UsedBookListResponse;

import java.util.List;

import static team.dankookie.server4983.book.domain.QBookImage.bookImage;
import static team.dankookie.server4983.book.domain.QUsedBook.usedBook;

@RequiredArgsConstructor
public class BookSalesDetailRepositoryImpl implements BookSalesDetailRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public List<UsedBookListResponse> getMyPageBookSalesDetailList(boolean canBuy, Long memberId) {
        JPAQuery<UsedBookListResponse> query = queryFactory.select(
                new QUsedBookListResponse(
                        usedBook.id,
                        JPAExpressions
                                .select(bookImage.imageUrl)
                                .from(bookImage)
                                .where(bookImage.usedBook.eq(usedBook)
                                        .and(bookImage.id.eq(
                                                JPAExpressions
                                                        .select(bookImage.id.min())
                                                        .from(bookImage)
                                                        .where(bookImage.usedBook.eq(usedBook))
                                        ))
                                ),
                        usedBook.bookStatus,
                        usedBook.name,
                        usedBook.tradeAvailableDatetime,
                        usedBook.createdAt,
                        usedBook.price
                )
        ).from(usedBook);
        return getMyPageBooksCanBuy(canBuy, memberId, query);
    }

    private List<UsedBookListResponse> getMyPageBooksCanBuy(boolean canBuy, Long memberId, JPAQuery<UsedBookListResponse> query) {
        if (canBuy) {
            return query
                    .where(
                            usedBook.bookStatus.eq(BookStatus.SALE)
                                    .and(
                                            usedBook.sellerMember.id.eq(memberId)
                                    ))
                    .orderBy(usedBook.createdAt.desc()).fetch();
        } else {
            return query
                    .where(
                            usedBook.bookStatus.eq(BookStatus.SOLD)
                                    .and(
                                            usedBook.sellerMember.id.eq(memberId)
                                    ))
                    .orderBy(usedBook.createdAt.desc()).fetch();
        }

    }
}
