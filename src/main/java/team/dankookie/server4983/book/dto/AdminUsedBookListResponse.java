package team.dankookie.server4983.book.dto;

import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import team.dankookie.server4983.book.constant.BookStatus;

@RequiredArgsConstructor
@Getter
public class AdminUsedBookListResponse {

    private final Long id;
    private final String bookStatus;
    private final String name;
    private final String publisher;
    private final Integer price;
    private final LocalDateTime createdAt;

    @QueryProjection
    public AdminUsedBookListResponse(Long id, BookStatus bookStatus, String name, String publisher, Integer price, LocalDateTime createdAt) {
        this.id = id;
        this.bookStatus = bookStatus.name();
        this.name = name;
        this.publisher = publisher;
        this.price = price;
        this.createdAt = createdAt;
    }
}

