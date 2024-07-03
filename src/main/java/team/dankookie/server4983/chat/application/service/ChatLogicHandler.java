package team.dankookie.server4983.chat.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import team.dankookie.server4983.chat.adapter.in.web.dto.ChatMessageResponse;
import team.dankookie.server4983.chat.adapter.in.web.dto.ChatRequest;
import team.dankookie.server4983.chat.adapter.out.persistence.ChatRoomRepository;
import team.dankookie.server4983.chat.application.service.chat_strategy.ChatStrategyFactory;
import team.dankookie.server4983.chat.domain.ChatRoom;
import team.dankookie.server4983.chat.exception.ChatException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatLogicHandler {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatStrategyFactory chatStrategyFactory;

    @Transactional
    public List<ChatMessageResponse> chatLogic(ChatRequest chatRequest) {
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatRequest.getChatRoomId())
                .orElseThrow(() -> new ChatException("채팅방을 찾을 수 없습니다."));

        ifChattingAlreadyFinishedThrowError(chatRoom);

        return chatStrategyFactory.getStrategy(chatRequest.getContentType())
                .execute(chatRoom, chatRequest);
    }

    private static void ifChattingAlreadyFinishedThrowError(ChatRoom chatRoom) {
        if (chatRoom.getInteractStep() == 999 || chatRoom.getInteractStep() == 1000 || chatRoom.getInteractStep() == 1001) {
            throw new ChatException("이미 종료된 거래입니다.");
        }
    }
}