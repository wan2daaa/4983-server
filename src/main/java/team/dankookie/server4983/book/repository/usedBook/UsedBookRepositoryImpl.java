package team.dankookie.server4983.book.repository.usedBook;

import static team.dankookie.server4983.book.domain.QBookImage.bookImage;
import static team.dankookie.server4983.book.domain.QUsedBook.usedBook;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import team.dankookie.server4983.book.constant.BookStatus;
import team.dankookie.server4983.book.constant.College;
import team.dankookie.server4983.book.constant.Department;
import team.dankookie.server4983.book.dto.AdminUsedBookListResponse;
import team.dankookie.server4983.book.dto.QAdminUsedBookListResponse;
import team.dankookie.server4983.book.dto.QUsedBookListResponse;
import team.dankookie.server4983.book.dto.UsedBookListResponse;

@RequiredArgsConstructor
public class UsedBookRepositoryImpl implements UsedBookRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<AdminUsedBookListResponse> getAdminUsedBookList(Pageable pageable,
                                                                String searchKeyword, BookStatus bookStatus) {

        if (bookStatus == BookStatus.ALL) {
            List<AdminUsedBookListResponse> content = jpaQueryFactory
                    .select(
                            new QAdminUsedBookListResponse(
                                    usedBook.id,
                                    usedBook.bookStatus,
                                    usedBook.name,
                                    usedBook.publisher,
                                    usedBook.price,
                                    usedBook.createdAt
                            )
                    ).from(usedBook)
                    .where(
                            boolNameOrPublisherContains(searchKeyword)
                    ).orderBy(usedBook.id.desc())
                    .offset(pageable.getOffset())
                    .limit(12)
                    .fetch();
            Long count = jpaQueryFactory
                    .select(usedBook.count())
                    .from(usedBook)
                    .where(
                            boolNameOrPublisherContains(searchKeyword)
                    ).fetchOne();
            return new PageImpl<>(content, pageable, count);
        }

        if (bookStatus == BookStatus.DELETE) {
            List<AdminUsedBookListResponse> content = jpaQueryFactory
                    .select(
                            new QAdminUsedBookListResponse(
                                    usedBook.id,
                                    usedBook.bookStatus,
                                    usedBook.name,
                                    usedBook.publisher,
                                    usedBook.price,
                                    usedBook.createdAt
                            )
                    ).from(usedBook)
                    .where(
                            usedBook.delYn.eq(true),
                            usedBook.isDeleted,
                            boolNameOrPublisherContains(searchKeyword)
                    ).orderBy(usedBook.id.desc())
                    .offset(pageable.getOffset())
                    .limit(12)
                    .fetch();
            Long count = jpaQueryFactory
                    .select(usedBook.count())
                    .from(usedBook)
                    .where(
                            usedBook.delYn.eq(true),
                            boolNameOrPublisherContains(searchKeyword)
                    ).fetchOne();
            return new PageImpl<>(content, pageable, count);
        }


        List<AdminUsedBookListResponse> content = jpaQueryFactory
                .select(
                        new QAdminUsedBookListResponse(
                                usedBook.id,
                                usedBook.bookStatus,
                                usedBook.name,
                                usedBook.publisher,
                                usedBook.price,
                                usedBook.createdAt
                        )
                ).from(usedBook)
                .where(
                        usedBook.bookStatus.eq(bookStatus),
                        boolNameOrPublisherContains(searchKeyword)
                ).orderBy(usedBook.id.desc())
                .offset(pageable.getOffset())
                .limit(12)
                .fetch();
        Long count = jpaQueryFactory
                .select(usedBook.count())
                .from(usedBook)
                .where(
                        usedBook.bookStatus.eq(bookStatus),
                        boolNameOrPublisherContains(searchKeyword)
                ).fetchOne();
        return new PageImpl<>(content, pageable, count);
    }

    @Override
    public List<UsedBookListResponse> getUsedBookList(boolean isOrderByTradeAvailableDatetime) {
        JPAQuery<UsedBookListResponse> query = jpaQueryFactory
                .select(
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
                        ))
                .from(usedBook);

        return getUsedBooksIsOrderByTradeAvailableDatetime(isOrderByTradeAvailableDatetime, query);
    }

    @Override
    public List<UsedBookListResponse> getUsedBookListInCollegeAndDepartment(List<College> college, List<Department> department, boolean isOrderByTradeAvailableDatetime) {
        JPAQuery<UsedBookListResponse> query = jpaQueryFactory.select(
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
                ).from(usedBook)
                .where(
                        usedBook.college.in(college).or(usedBook.department.in(department))
                );

        return getUsedBooksIsOrderByTradeAvailableDatetime(isOrderByTradeAvailableDatetime, query);
    }

    @Override
    public List<UsedBookListResponse> getUsedBookListBySearchKeyword(String searchKeyword, boolean isOrderByTradeAvailableDatetime) {
        JPAQuery<UsedBookListResponse> query = jpaQueryFactory.select(
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
                ).from(usedBook)
                .where(
                        usedBook.name.contains(searchKeyword)
                );
        return getUsedBooksIsOrderByTradeAvailableDatetime(isOrderByTradeAvailableDatetime, query);
    }

    private List<UsedBookListResponse> getUsedBooksIsOrderByTradeAvailableDatetime(boolean isOrderByTradeAvailableDatetime, JPAQuery<UsedBookListResponse> query) {
        if (isOrderByTradeAvailableDatetime) {
            return query
                    .where(usedBook.isDeleted.eq(false),
                            usedBook.sellerMember.isWithdraw.eq(false)
                    )
                    .orderBy(
                            new CaseBuilder()
                                    .when(usedBook.bookStatus.eq(BookStatus.SALE)).then(1)
                                    .when(usedBook.bookStatus.eq(BookStatus.TRADE)).then(2)
                                    .when(usedBook.bookStatus.eq(BookStatus.SOLD)).then(3)
                                    .otherwise(4)
                                    .asc(),
                            usedBook.tradeAvailableDatetime.desc()
                    ).fetch();
        } else {

            return query
                    .where(usedBook.isDeleted.eq(false),
                            usedBook.sellerMember.isWithdraw.eq(false)
                    )
                    .orderBy(
                            new CaseBuilder()
                                    .when(usedBook.bookStatus.eq(BookStatus.SALE)).then(1)
                                    .when(usedBook.bookStatus.eq(BookStatus.TRADE)).then(2)
                                    .when(usedBook.bookStatus.eq(BookStatus.SOLD)).then(3)
                                    .otherwise(4)
                                    .asc(),
                            usedBook.id.desc()
                    )
                    .fetch();
        }
    }

    private BooleanExpression boolNameOrPublisherContains(String searchKeyword) {
        return usedBook.name.contains(searchKeyword).or(usedBook.publisher.contains(searchKeyword));
    }
}
