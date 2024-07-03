package team.dankookie.server4983.chat.application.service.chat_strategy;

import org.springframework.stereotype.Component;
import team.dankookie.server4983.book.repository.locker.LockerRepository;
import team.dankookie.server4983.chat.adapter.in.web.dto.ChatMessageResponse;
import team.dankookie.server4983.chat.adapter.in.web.dto.ChatRequest;
import team.dankookie.server4983.chat.adapter.out.persistence.BuyerChatRepository;
import team.dankookie.server4983.chat.adapter.out.persistence.SellerChatRepository;
import team.dankookie.server4983.chat.domain.BuyerChat;
import team.dankookie.server4983.chat.domain.ChatRoom;
import team.dankookie.server4983.chat.exception.ChatException;
import team.dankookie.server4983.fcm.service.FcmService;
import team.dankookie.server4983.member.domain.Member;
import team.dankookie.server4983.sms.service.CoolSmsService;

import java.util.List;

import static team.dankookie.server4983.chat.constant.ContentType.TRADE_COMPLETE_BUYER;
import static team.dankookie.server4983.chat.constant.ContentType.TRADE_COMPLETE_SELLER;

@Component
public class TradeCompleteStrategy extends ChatStrategy {

    public TradeCompleteStrategy(BuyerChatRepository buyerChatRepository, SellerChatRepository sellerChatRepository, FcmService fcmService, CoolSmsService smsService, LockerRepository lockerRepository) {
        super(buyerChatRepository, sellerChatRepository, fcmService, smsService, lockerRepository);
    }

    @Override
    public List<ChatMessageResponse> execute(ChatRoom chatRoom, ChatRequest chatRequest) {
        Member buyer = chatRoom.getBuyer();
        Member seller = chatRoom.getSeller();

        if (chatRoom.getInteractStep() >= 6) {
            throw new ChatException("이미 거래를 완료했습니다.");
        }

        releaseLocker(chatRoom);

        String message = """
                거래가 완료되었습니다.
                이용해주셔서 감사합니다.

                -사고파삼-""";

        BuyerChat buyerChat = saveAndSendMessage(chatRoom, message, seller, buyer);
        buyerChat.updateIsReadTrue();

        chatRoom.setInteractStep(6);

        return List.of(buyerChat.toChatMessageResponse());
    }

    private BuyerChat saveAndSendMessage(ChatRoom chatRoom, String message, Member seller, Member buyer) {
        saveSellerChat(chatRoom, TRADE_COMPLETE_SELLER, message);
        BuyerChat buyerChat = saveBuyerChat(chatRoom, TRADE_COMPLETE_BUYER, message);

        sendNotification(seller, message);
        sendNotification(buyer, message);
        return buyerChat;
    }


}
