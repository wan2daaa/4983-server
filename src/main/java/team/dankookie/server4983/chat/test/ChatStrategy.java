package team.dankookie.server4983.chat.test;

import team.dankookie.server4983.chat.domain.ChatRoom;
import team.dankookie.server4983.chat.dto.ChatMessageResponse;
import team.dankookie.server4983.chat.dto.ChatRequest;

import java.util.List;

public interface ChatStrategy {
    List<ChatMessageResponse> execute(ChatRoom chatRoom, ChatRequest chatRequest);

}
