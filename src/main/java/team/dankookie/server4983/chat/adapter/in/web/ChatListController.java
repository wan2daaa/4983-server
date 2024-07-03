package team.dankookie.server4983.chat.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.dankookie.server4983.chat.adapter.in.web.dto.ChatListResponse;
import team.dankookie.server4983.chat.application.port.in.ChatListUseCase;
import team.dankookie.server4983.jwt.dto.AccessToken;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ChatListController {

    private final ChatListUseCase chatListUseCase;

    @GetMapping("/chats/lists")
    public List<ChatListResponse> getChatList(AccessToken accessToken) {
        return chatListUseCase.getChatListWithMemberNickname(accessToken.nickname());
    }
}
