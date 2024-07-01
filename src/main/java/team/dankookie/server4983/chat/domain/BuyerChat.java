package team.dankookie.server4983.chat.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import team.dankookie.server4983.chat.constant.ContentType;
import team.dankookie.server4983.chat.dto.ChatMessageResponse;

@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Getter
@Entity
public class BuyerChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @ColumnDefault("false")
    @Builder.Default
    private Boolean isRead = false;

    @Column(nullable = false)
    private String message;

    @Column(updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    public static BuyerChat buildBuyerChat(String message, ContentType contentType) {
        return BuyerChat.builder()
                .message(message)
                .contentType(contentType)
                .build();
    }

    public static BuyerChat buildBuyerChat(String message, ContentType contentType, ChatRoom chatRoom) {
        return BuyerChat.builder()
                .message(message)
                .contentType(contentType)
                .chatRoom(chatRoom)
                .build();
    }

    public ChatMessageResponse toChatMessageResponse() {
        return ChatMessageResponse.of(
                chatRoom.getChatRoomId(),
                message,
                contentType,
                createdAt
        );
    }

    public void updateIsReadTrue() {
        this.isRead = true;
    }
}
