package team.dankookie.server4983.chat.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.dankookie.server4983.chat.adapter.in.web.dto.ChatMessageResponse;
import team.dankookie.server4983.chat.adapter.in.web.dto.ChatRequest;
import team.dankookie.server4983.chat.adapter.out.persistence.ChatRoomRepository;
import team.dankookie.server4983.chat.application.port.in.ChatInteractUseCase;
import team.dankookie.server4983.chat.exception.ChatException;
import team.dankookie.server4983.member.domain.Member;
import team.dankookie.server4983.member.service.MemberService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatInteractService implements ChatInteractUseCase {

    private final ChatLogicHandler chatLogicHandler;
    private final MemberService memberService;
    private final ChatRoomRepository chatRoomRepository;


    @Override
    public List<ChatMessageResponse> interactUserChat(ChatRequest chatRequest, String nickname) {
        ifMemberNotInChatRoomThenThrow(chatRequest, nickname);

        return chatLogicHandler.chatLogic(chatRequest);
    }

    private void ifMemberNotInChatRoomThenThrow(ChatRequest chatRequest, String nickname) {
        Member member = memberService.getMemberByNickname(nickname);

        chatRoomRepository.findBySellerOrBuyerAndChatRoomId(member.getId(), member.getId(),
                        chatRequest.getChatRoomId())
                .orElseThrow(() -> new ChatException("해당 채팅방에 존재하지 않는 사용자입니다."));
    }
}
