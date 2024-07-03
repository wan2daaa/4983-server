package team.dankookie.server4983.chat.adapter.in.web.dto;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoomRequest {
    private Long usedBookId;

    public static ChatRoomRequest of(Long usedBookId) {
        return ChatRoomRequest.builder().usedBookId(usedBookId).build();
    }

}
