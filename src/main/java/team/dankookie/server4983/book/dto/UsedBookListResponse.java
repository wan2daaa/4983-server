package team.dankookie.server4983.book.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import team.dankookie.server4983.book.constant.BookStatus;

import java.time.LocalDateTime;

public record UsedBookListResponse(
        Long usedBookId,
        String imageUrl,
        BookStatus bookStatus,
        String name,
        LocalDateTime tradeAvailableDatetime,
        LocalDateTime createdAt,
        Integer price
) {

    @Builder
    @QueryProjection
    public UsedBookListResponse(Long usedBookId, String imageUrl, BookStatus bookStatus, String name, LocalDateTime tradeAvailableDatetime, LocalDateTime createdAt, Integer price) {
        this.usedBookId = usedBookId;
        this.imageUrl = imageUrl;
        this.bookStatus = bookStatus;
        this.name = name;
        this.tradeAvailableDatetime = tradeAvailableDatetime;
        this.createdAt = createdAt;
        this.price = price;
    }

    public static UsedBookListResponse of(Long usedBookId, String imageUrl, BookStatus bookStatus, String name, LocalDateTime tradeAvailableDatetime, LocalDateTime createdAt, Integer price) {
        return new UsedBookListResponse(usedBookId, imageUrl, bookStatus, name, tradeAvailableDatetime, createdAt, price);
    }
}

