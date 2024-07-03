package team.dankookie.server4983.chat.adapter.in.web.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import team.dankookie.server4983.chat.constant.ContentType;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatRequest {

    private Long chatRoomId;

    private ContentType contentType;

    private String message = "";


    public static ChatRequest of(Long chatRoomId, ContentType contentType) {
        return new ChatRequest(chatRoomId, contentType, "");
    }

    public static ChatRequest of(Long chatRoomId, ContentType contentType, String message) {
        return new ChatRequest(chatRoomId, contentType, message);
    }
}
