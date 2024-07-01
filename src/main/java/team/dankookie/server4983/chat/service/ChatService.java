package team.dankookie.server4983.chat.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.dankookie.server4983.book.domain.Locker;
import team.dankookie.server4983.book.domain.UsedBook;
import team.dankookie.server4983.book.repository.locker.LockerRepository;
import team.dankookie.server4983.book.repository.usedBook.UsedBookRepository;
import team.dankookie.server4983.chat.domain.BuyerChat;
import team.dankookie.server4983.chat.domain.ChatRoom;
import team.dankookie.server4983.chat.domain.SellerChat;
import team.dankookie.server4983.chat.dto.*;
import team.dankookie.server4983.chat.exception.ChatException;
import team.dankookie.server4983.chat.handler.ChatBotAdmin;
import team.dankookie.server4983.chat.handler.ChatLogicHandler;
import team.dankookie.server4983.chat.repository.BuyerChatRepository;
import team.dankookie.server4983.chat.repository.ChatRoomRepository;
import team.dankookie.server4983.chat.repository.SellerChatRepository;
import team.dankookie.server4983.jwt.constants.TokenSecretKey;
import team.dankookie.server4983.jwt.dto.AccessToken;
import team.dankookie.server4983.jwt.util.JwtTokenUtils;
import team.dankookie.server4983.member.domain.Member;
import team.dankookie.server4983.member.repository.MemberRepository;
import team.dankookie.server4983.member.service.MemberService;

import javax.security.auth.login.AccountException;
import java.util.List;
import java.util.Optional;

import static team.dankookie.server4983.chat.constant.ContentType.*;
import static team.dankookie.server4983.chat.domain.ChatRoom.buildChatRoom;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatLogicHandler chatLogicHandler;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final UsedBookRepository usedBookRepository;
    private final MemberService memberService;
    private final JwtTokenUtils jwtTokenUtils;
    private final ChatBotAdmin chatBotAdmin;
    private final LockerRepository lockerRepository;
    private final BuyerChatRepository buyerChatRepository;
    private final SellerChatRepository sellerChatRepository;

    public ChatRoom findByChatRoomId(long chatRoomId) {
        return chatRoomRepository.findByChatRoomId(chatRoomId)
                .orElseThrow(() -> new ChatException("채팅방을 찾을 수 없습니다."));
    }

    public List<ChatMessageResponse> chatRequestHandler(ChatRequest chatRequest,
                                                        AccessToken accessToken) {

        Member member = memberService.findMemberByNickname(accessToken.nickname());

        chatRoomRepository.findBySellerOrBuyerAndChatRoomId(member.getId(), member.getId(),
                        chatRequest.getChatRoomId())
                .orElseThrow(() -> new ChatException("해당 채팅방에 존재하지 않는 사용자입니다."));

        return chatLogicHandler.chatLogic(chatRequest);
    }

    @Transactional
    public ChatRoomResponse createChatRoom(ChatRoomRequest chatRoomRequest, AccessToken accessToken)
            throws AccountException {
        String buyerNickname = accessToken.nickname();

        UsedBook usedBook = usedBookRepository.findById(chatRoomRequest.getUsedBookId())
                .orElseThrow(() -> new ChatException("거래 글을 찾을 수 없습니다."));

        Member seller = usedBook.getSellerMember();
        Member buyer = memberRepository.findByNickname(buyerNickname)
                .orElseThrow(() -> new ChatException("사용자를 찾을 수 없습니다."));

        if (seller.getNickname().equals(buyerNickname)) {
            throw new ChatException("자신의 판매글에 거래요청을 할 수 없습니다.");
        }

        ChatRoomResponse result = isChatRoomAlreadyExistsBySellerBuyerUsedBook(usedBook, seller,
                buyer);
        if (result != null) {
            return result;
        }

        ChatRoom savedChatroom = buildAndSaveChatRoom(usedBook, seller, buyer);

        ChatRequest chatRequest = ChatRequest.of(savedChatroom.getChatRoomId(), BOOK_PURCHASE_START);
        chatRequestHandler(chatRequest, accessToken);

        return ChatRoomResponse.of(savedChatroom.getChatRoomId());
    }

    private ChatRoomResponse isChatRoomAlreadyExistsBySellerBuyerUsedBook(UsedBook usedBook,
                                                                          Member seller, Member buyer) {
        Optional<ChatRoom> result = chatRoomRepository.findBookBySellerAndBuyerAndBook(seller,
                buyer, usedBook);
        return result.map(chatRoom -> ChatRoomResponse.of(chatRoom.getChatRoomId())).orElse(null);
    }

    private ChatRoom buildAndSaveChatRoom(UsedBook usedBook, Member seller, Member buyer) {
        ChatRoom chatRoom = buildChatRoom(buyer, seller, usedBook);
        ChatRoom savedChatroom = chatRoomRepository.save(chatRoom);
        return savedChatroom;
    }

    public ChatRoomResponse getChatRoom(Long chatRoom, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String userName = jwtTokenUtils.getNickname(token);

        ChatRoom result = chatRoomRepository.findById(chatRoom)
                .orElseThrow(() -> new ChatException("존재하지 않는 채팅방 입니다."));

        return ChatRoomResponse.of(result.getChatRoomId());
    }

    @Transactional
    public List<ChatMessageResponse> getChattingData(long chatRoomId, AccessToken accessToken) {

        String nickname = accessToken.nickname();

        boolean isMemberBuyer = chatRoomRepository.existsByChatRoomIdAndBuyer_Nickname(chatRoomId,
                nickname);

        if (!isMemberBuyer) {
            boolean isMemberSeller = chatRoomRepository.existsByChatRoomIdAndSeller_Nickname(chatRoomId,
                    nickname);

            if (!isMemberSeller) {
                throw new ChatException("채팅방에 속해있지 않습니다.");
            } else {
                chatRoomRepository.updateSellerChattingToRead(chatRoomId);
                return chatRoomRepository.findChatMessageByChatroomIdWithSellerNickname(chatRoomId,
                        nickname);
            }
        } else {
            chatRoomRepository.updateBuyerChattingToRead(chatRoomId);
            return chatRoomRepository.findChatMessageByChatroomIdWithBuyerNickname(chatRoomId, nickname);
        }
    }

    @Transactional
    public List<ChatMessageResponse> getNotReadChattingData(long chatRoomId,
                                                            AccessToken accessToken) {
        ChatRoom chatRoom = findByChatRoomId(chatRoomId);

        if (chatRoom.getSeller().getNickname().equals(accessToken.nickname())) {
            List<ChatMessageResponse> responseList = chatRoomRepository.getNotReadSellerChattingData(
                    chatRoomId);
            chatRoomRepository.modifySellerChattingToRead(chatRoomId);
            return responseList;
        }

        if (chatRoom.getBuyer().getNickname().equals(accessToken.nickname())) {
            List<ChatMessageResponse> responseList = chatRoomRepository.getNotReadBuyerChattingData(
                    chatRoomId);
            chatRoomRepository.modifyBuyerChattingToRead(chatRoomId);
            return responseList;
        }

        throw new ChatException("해당 채팅방에 존재하지 않는 회원입니다.");
    }

    public List<ChatListResponse> getChatListWithAccessToken(AccessToken accessToken) {
        String nickname = jwtTokenUtils.getNickname(accessToken.value());

        return chatRoomRepository.findByChatroomListWithNickname(nickname);
    }

    @Transactional
    public void stopTrade(ChatStopRequest chatStopRequest, AccessToken accessToken) {
        String accessTokenNickname = accessToken.nickname();

        ChatRoom chatRoom = findByChatRoomId(chatStopRequest.getChatRoomId());

        Member seller = chatRoomRepository.getSeller(chatStopRequest.getChatRoomId());
        Member buyer = chatRoomRepository.getBuyer(chatStopRequest.getChatRoomId());

        if (seller.getNickname().equals(accessTokenNickname)) {
            chatBotAdmin.tradeStopBySeller(chatRoom, seller, buyer);
        } else if (buyer.getNickname().equals(accessTokenNickname)) {
            chatBotAdmin.tradeStopByBuyer(chatRoom, seller, buyer);
        } else {
            throw new ChatException("해당 채팅방에 존재하지 않는 회원입니다.");
        }

        if (chatRoom.getInteractStep() > 100 || chatRoom.getInteractStep() == 6) {
            throw new ChatException("이미 거래가 종료되었습니다.");
        }
        if (chatRoom.getInteractStep() == 4 || chatRoom.getInteractStep() > 5) {
            releaseLocker(chatRoom);
        }

        chatRoom.setInteractStep(999);
    }

    public void releaseLocker(ChatRoom chatRoom) {
        Locker locker = lockerRepository.findByChatRoom(chatRoom)
                .orElseThrow(() -> new ChatException("해당 채팅방에 사물함이 없습니다."));

        locker.releaseLocker();
    }

    public void sendCustomMessage(long chatRoomId, ChatMessageRequest messageRequest) {
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId)
                .orElseThrow(() -> new ChatException("채팅방을 찾을 수 없습니다."));

        if (messageRequest.getType().equals("seller")) {
            SellerChat sellerChat = SellerChat.buildSellerChat(messageRequest.getMessage(), CUSTOM_SELLER,
                    chatRoom);
            chatRoom.addSellerChat(sellerChat);

            sellerChatRepository.save(sellerChat);

        } else if (messageRequest.getType().equals("buyer")) {
            BuyerChat buyerChat = BuyerChat.buildBuyerChat(messageRequest.getMessage(), CUSTOM_BUYER
                    , chatRoom);
            chatRoom.addBuyerChat(buyerChat);

            buyerChatRepository.save(buyerChat);
        } else {
            throw new ChatException("잘못된 타입입니다.");
        }
    }
}
