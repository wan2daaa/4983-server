package team.dankookie.server4983.chat.adapter.in.web.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatRoomResponse {

    private final long chatRoomId;

    public static ChatRoomResponse of(Long chatRoomId) {
        return new ChatRoomResponse(chatRoomId);
    }

}
