package team.dankookie.server4983.chat.application.port.in;

import team.dankookie.server4983.chat.adapter.in.web.dto.ChatMessageResponse;
import team.dankookie.server4983.chat.adapter.in.web.dto.ChatRequest;

import java.util.List;

public interface ChatInteractUseCase {


    List<ChatMessageResponse> interactUserChat(ChatRequest chatRequest, String nickname);
}
