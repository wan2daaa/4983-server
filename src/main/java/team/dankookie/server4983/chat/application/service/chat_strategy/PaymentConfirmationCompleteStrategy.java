package team.dankookie.server4983.chat.application.service.chat_strategy;

import org.springframework.stereotype.Component;
import team.dankookie.server4983.book.repository.locker.LockerRepository;
import team.dankookie.server4983.chat.adapter.in.web.dto.ChatMessageResponse;
import team.dankookie.server4983.chat.adapter.in.web.dto.ChatRequest;
import team.dankookie.server4983.chat.adapter.out.persistence.BuyerChatRepository;
import team.dankookie.server4983.chat.adapter.out.persistence.SellerChatRepository;
import team.dankookie.server4983.chat.domain.ChatRoom;
import team.dankookie.server4983.chat.exception.ChatException;
import team.dankookie.server4983.fcm.service.FcmService;
import team.dankookie.server4983.member.domain.Member;
import team.dankookie.server4983.sms.service.CoolSmsService;

import java.util.List;

import static team.dankookie.server4983.chat.constant.ContentType.PAYMENT_CONFIRMATION_COMPLETE_BUYER;
import static team.dankookie.server4983.chat.constant.ContentType.PAYMENT_CONFIRMATION_COMPLETE_SELLER;

@Component
public class PaymentConfirmationCompleteStrategy extends ChatStrategy {

    public PaymentConfirmationCompleteStrategy(BuyerChatRepository buyerChatRepository, SellerChatRepository sellerChatRepository, FcmService fcmService, CoolSmsService smsService, LockerRepository lockerRepository) {
        super(buyerChatRepository, sellerChatRepository, fcmService, smsService, lockerRepository);
    }

    @Override
    public List<ChatMessageResponse> execute(ChatRoom chatRoom, ChatRequest chatRequest) {
        Member buyer = chatRoom.getBuyer();
        Member seller = chatRoom.getSeller();

        if (chatRoom.getInteractStep() >= 3) {
            throw new ChatException("이미 입금 확인을 했습니다.");
        }

        String sellerMessage = """
                입금이 확인되었습니다.\s
                구매자가 “거래 완료" 버튼을 클릭 후\s
                판매 금액이 자동으로 입금될 예정입니다.

                책을 넣을 사물함을 선택하고 비밀번호를 설정해주세요.""";
        String buyerMessage = """
                입금이 확인되었습니다.

                거래날짜에 판매자가 사물함에 서적을 배치할 예정입니다.""";
        saveAndSendMessage(chatRoom, sellerMessage, buyerMessage, seller, buyer);

        chatRoom.setInteractStep(3);

        return List.of();
    }

    private void saveAndSendMessage(ChatRoom chatRoom, String sellerMessage, String buyerMessage, Member seller, Member buyer) {
        saveSellerChat(chatRoom, PAYMENT_CONFIRMATION_COMPLETE_SELLER, sellerMessage);
        saveBuyerChat(chatRoom, PAYMENT_CONFIRMATION_COMPLETE_BUYER, buyerMessage);

        sendNotification(seller, sellerMessage);
        sendNotification(buyer, buyerMessage);
    }
}
