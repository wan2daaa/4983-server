package team.dankookie.server4983.chat.adapter.in.web.dto;

import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;

public record ChatListResponse(
        long chatRoomId,
        String usedBookName,
        String message,
        LocalDateTime createdAt,
        boolean isRead,
        String imageUrl
) {
    public static ChatListResponse of(
            long chatRoomId,
            String usedBookName,
            String message,
            LocalDateTime createdAt,
            boolean isRead,
            String imageUrl
    ) {
        return new ChatListResponse(
                chatRoomId,
                usedBookName,
                message,
                createdAt,
                isRead,
                imageUrl
        );
    }

    @QueryProjection
    public ChatListResponse(long chatRoomId, String usedBookName, String message,
                            LocalDateTime createdAt, boolean isRead, String imageUrl) {
        this.chatRoomId = chatRoomId;
        this.usedBookName = usedBookName;
        this.message = message;
        this.createdAt = createdAt;
        this.isRead = isRead;
        this.imageUrl = imageUrl;
    }
}
