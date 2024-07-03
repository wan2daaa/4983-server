package team.dankookie.server4983.chat.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.dankookie.server4983.chat.adapter.in.web.dto.ChatMessageResponse;
import team.dankookie.server4983.chat.adapter.out.persistence.ChatRoomRepository;
import team.dankookie.server4983.chat.application.port.in.FindChattingUseCase;
import team.dankookie.server4983.chat.exception.ChatException;
import team.dankookie.server4983.jwt.dto.AccessToken;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FindChattingService implements FindChattingUseCase {

    private final ChatRoomRepository chatRoomRepository;

    @Override
    public List<ChatMessageResponse> getChattingData(long chatRoomId, AccessToken accessToken) {
        String nickname = accessToken.nickname();

        if (isUserInChatRoom(chatRoomId, nickname)) {
            return getChatMessagesForUser(chatRoomId, nickname);
        } else {
            throw new ChatException("채팅방에 속해있지 않습니다.");
        }
    }

    private boolean isUserInChatRoom(long chatRoomId, String nickname) {
        return chatRoomRepository.existsByChatRoomIdAndBuyer_Nickname(chatRoomId, nickname)
                ||
                chatRoomRepository.existsByChatRoomIdAndSeller_Nickname(chatRoomId, nickname);
    }

    private List<ChatMessageResponse> getChatMessagesForUser(long chatRoomId, String nickname) {
        if (chatRoomRepository.existsByChatRoomIdAndBuyer_Nickname(chatRoomId, nickname)) {
            chatRoomRepository.updateBuyerChattingToRead(chatRoomId);

            return chatRoomRepository.findChatMessageByChatroomIdWithBuyerNickname(chatRoomId, nickname);
        } else {
            chatRoomRepository.updateSellerChattingToRead(chatRoomId);

            return chatRoomRepository.findChatMessageByChatroomIdWithSellerNickname(chatRoomId, nickname);
        }
    }
}
