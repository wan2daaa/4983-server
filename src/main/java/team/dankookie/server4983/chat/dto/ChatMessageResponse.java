package team.dankookie.server4983.chat.dto;

import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;

import team.dankookie.server4983.chat.constant.ContentType;

public record ChatMessageResponse(
        long chatRoomId,
        String message,
        ContentType contentType,
        LocalDateTime createdAt
) {

    public static ChatMessageResponse of(
            long chatRoomId,
            String message,
            ContentType contentType,
            LocalDateTime createdAt
    ) {
        return new ChatMessageResponse(
                chatRoomId,
                message,
                contentType,
                createdAt
        );
    }

    @QueryProjection
    public ChatMessageResponse(long chatRoomId, String message, ContentType contentType, LocalDateTime createdAt) {
        this.chatRoomId = chatRoomId;
        this.message = message;
        this.contentType = contentType;
        this.createdAt = createdAt;
    }
}
