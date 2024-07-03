package team.dankookie.server4983.chat.application.port.in;

import team.dankookie.server4983.chat.adapter.in.web.dto.ChatListResponse;

import java.util.List;

public interface ChatListUseCase {
    List<ChatListResponse> getChatListWithMemberNickname(String nickname);
}
