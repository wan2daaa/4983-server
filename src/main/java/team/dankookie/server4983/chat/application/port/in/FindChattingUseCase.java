package team.dankookie.server4983.chat.application.port.in;

import team.dankookie.server4983.chat.adapter.in.web.dto.ChatMessageResponse;
import team.dankookie.server4983.jwt.dto.AccessToken;

import java.util.List;

public interface FindChattingUseCase {
    List<ChatMessageResponse> getChattingData(long chatRoomId, AccessToken accessToken);
}
