package team.dankookie.server4983.chat.application.port.out;

import team.dankookie.server4983.chat.adapter.in.web.dto.ChatListResponse;

import java.util.List;

public interface ChatListPort {
    List<ChatListResponse> getChatListWithMemberNickname(String nickname);
}
