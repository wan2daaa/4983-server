package team.dankookie.server4983.chat.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.dankookie.server4983.chat.adapter.in.web.dto.ChatListResponse;
import team.dankookie.server4983.chat.application.port.in.ChatListUseCase;
import team.dankookie.server4983.chat.application.port.out.ChatListPort;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatListService implements ChatListUseCase {

    private final ChatListPort chatListPort;

    @Override
    public List<ChatListResponse> getChatListWithMemberNickname(String nickname) {

        return chatListPort.getChatListWithMemberNickname(nickname);
    }
}
