package team.dankookie.server4983.chat.application.service.chat_strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team.dankookie.server4983.book.domain.Locker;
import team.dankookie.server4983.book.repository.locker.LockerRepository;
import team.dankookie.server4983.chat.adapter.in.web.dto.ChatMessageResponse;
import team.dankookie.server4983.chat.adapter.in.web.dto.ChatRequest;
import team.dankookie.server4983.chat.adapter.out.persistence.BuyerChatRepository;
import team.dankookie.server4983.chat.adapter.out.persistence.SellerChatRepository;
import team.dankookie.server4983.chat.constant.ContentType;
import team.dankookie.server4983.chat.domain.BuyerChat;
import team.dankookie.server4983.chat.domain.ChatRoom;
import team.dankookie.server4983.chat.domain.SellerChat;
import team.dankookie.server4983.chat.exception.ChatException;
import team.dankookie.server4983.fcm.dto.FcmTargetUserIdRequest;
import team.dankookie.server4983.fcm.service.FcmService;
import team.dankookie.server4983.member.domain.Member;
import team.dankookie.server4983.sms.service.CoolSmsService;

import java.util.List;

@Component
@RequiredArgsConstructor
public abstract class ChatStrategy {
    private final BuyerChatRepository buyerChatRepository;
    private final SellerChatRepository sellerChatRepository;
    private final FcmService fcmService;
    private final CoolSmsService smsService;
    protected final LockerRepository lockerRepository;


    public abstract List<ChatMessageResponse> execute(ChatRoom chatRoom, ChatRequest chatRequest);

    protected BuyerChat saveBuyerChat(ChatRoom chatRoom, ContentType contentType, String buyerMessage) {
        BuyerChat buyerChat = BuyerChat.buildBuyerChat(buyerMessage, contentType, chatRoom);
        buyerChatRepository.save(buyerChat);
        chatRoom.addBuyerChat(buyerChat);
        return buyerChat;
    }

    protected SellerChat saveSellerChat(ChatRoom chatRoom, ContentType contentType, String sellerMessage) {
        SellerChat sellerChat = SellerChat.buildSellerChat(sellerMessage, contentType, chatRoom);
        sellerChatRepository.save(sellerChat);
        chatRoom.addSellerChat(sellerChat);
        return sellerChat;
    }

    protected void sendNotification(Member member, String message) {
        fcmService.sendChattingNotificationByToken(
                FcmTargetUserIdRequest.of(member.getId(), message)
        );
    }

    protected void sendSmsToAdmin(String message) {
        smsService.sendAdminToSms(message);
    }

    protected Locker getLocker(ChatRoom chatRoom) {
        return lockerRepository.findByChatRoom(chatRoom)
                .orElseThrow(() -> new ChatException("서적을 배치할 사물함을 찾을 수 없습니다."));
    }

    protected void releaseLocker(ChatRoom chatRoom) {
        getLocker(chatRoom).releaseLocker();
    }
}
