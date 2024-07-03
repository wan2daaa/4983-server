package team.dankookie.server4983.chat.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.dankookie.server4983.book.domain.UsedBook;
import team.dankookie.server4983.book.service.UsedBookService;
import team.dankookie.server4983.chat.adapter.in.web.dto.ChatRequest;
import team.dankookie.server4983.chat.adapter.in.web.dto.ChatRoomResponse;
import team.dankookie.server4983.chat.adapter.out.persistence.ChatRoomRepository;
import team.dankookie.server4983.chat.application.port.in.CreateChatRoomCommand;
import team.dankookie.server4983.chat.application.port.in.CreateChatRoomUseCase;
import team.dankookie.server4983.chat.application.port.out.ExistChatRoomPort;
import team.dankookie.server4983.chat.domain.ChatRoom;
import team.dankookie.server4983.member.domain.Member;
import team.dankookie.server4983.member.service.MemberService;

import static team.dankookie.server4983.chat.constant.ContentType.BOOK_PURCHASE_START;

@Transactional
@RequiredArgsConstructor
@Service
public class CreateChatRoomService implements CreateChatRoomUseCase {

    private final UsedBookService usedBookService;
    private final MemberService memberService;
    private final ChatRoomRepository chatRoomRepository;
    private final ExistChatRoomPort existChatRoomPort;
    private final ChatLogicHandler chatLogicHandler;

    @Override
    public ChatRoomResponse createChatRoom(CreateChatRoomCommand command) {

        UsedBook usedBook = usedBookService.getUsedBookById(command.usedBookId());
        Member seller = usedBook.getSellerMember();
        Member buyer = memberService.getMemberByNickname(command.nickname());

        ifChatRoomExistThenThrowException(seller, buyer, usedBook);

        ChatRoom chatRoom = ChatRoom.create(usedBook, seller, buyer);
        chatRoomRepository.save(chatRoom);

        ChatRequest chatRequest = ChatRequest.of(chatRoom.getChatRoomId(), BOOK_PURCHASE_START);
        chatLogicHandler.chatLogic(chatRequest);

        return null;
    }

    private void ifChatRoomExistThenThrowException(Member seller, Member buyer, UsedBook usedBook) {
        existChatRoomPort.existsBySellerAndBuyerAndUsedBook(seller, buyer, usedBook)
                .ifPresent(chatRoom -> {
                    throw new IllegalArgumentException("이미 채팅방이 존재합니다.");
                });
    }
}
