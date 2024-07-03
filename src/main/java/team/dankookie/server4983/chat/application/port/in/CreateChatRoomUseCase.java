package team.dankookie.server4983.chat.application.port.in;

import team.dankookie.server4983.chat.adapter.in.web.dto.ChatRoomResponse;

public interface CreateChatRoomUseCase {
    ChatRoomResponse createChatRoom(CreateChatRoomCommand command);
}
