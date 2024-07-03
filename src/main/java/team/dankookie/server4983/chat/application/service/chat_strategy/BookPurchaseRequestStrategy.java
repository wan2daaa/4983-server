package team.dankookie.server4983.chat.application.service.chat_strategy;

import org.springframework.stereotype.Component;
import team.dankookie.server4983.book.repository.locker.LockerRepository;
import team.dankookie.server4983.chat.adapter.in.web.dto.ChatMessageResponse;
import team.dankookie.server4983.chat.adapter.in.web.dto.ChatRequest;
import team.dankookie.server4983.chat.adapter.out.persistence.BuyerChatRepository;
import team.dankookie.server4983.chat.adapter.out.persistence.SellerChatRepository;
import team.dankookie.server4983.chat.domain.ChatRoom;
import team.dankookie.server4983.chat.domain.SellerChat;
import team.dankookie.server4983.chat.exception.ChatException;
import team.dankookie.server4983.fcm.service.FcmService;
import team.dankookie.server4983.member.domain.Member;
import team.dankookie.server4983.sms.service.CoolSmsService;

import java.util.List;

import static team.dankookie.server4983.chat.constant.ContentType.BOOK_PURCHASE_REQUEST_BUYER;
import static team.dankookie.server4983.chat.constant.ContentType.BOOK_PURCHASE_REQUEST_SELLER;

@Component
public class BookPurchaseRequestStrategy extends ChatStrategy {

    public BookPurchaseRequestStrategy(BuyerChatRepository buyerChatRepository, SellerChatRepository sellerChatRepository, FcmService fcmService, CoolSmsService smsService, LockerRepository lockerRepository) {
        super(buyerChatRepository, sellerChatRepository, fcmService, smsService, lockerRepository);
    }

    @Override
    public List<ChatMessageResponse> execute(ChatRoom chatRoom, ChatRequest chatRequest) {
        Member buyer = chatRoom.getBuyer();
        Member seller = chatRoom.getSeller();

        if (chatRoom.getInteractStep() >= 2) {
            throw new ChatException("이미 거래 요청을 수락했습니다.");
        }

        chatRoom.setInteractStep(2);

        String sellerMessage = """
                구매자에게 요청 수락 알림을 보냈습니다.\s
                입금이 확인될 때 까지 기다려주세요. :)""";

        String buyerMessage = String.format("""
                        '%s' 님께 '%s' 서적 거래를 요청을 수락했습니다.\s
                        아래 계좌정보로 결제금액을 송금하여 주십시오.\s

                        계좌번호:
                        카카오 7979-86-67501 (사고파삼)\s
                        결제 금액 : %d원""",
                chatRoom.getSeller().getNickname(),
                chatRoom.getUsedBook().getName(),
                chatRoom.getUsedBook().getPrice());
        SellerChat sellerChat = saveAndSendMessage(chatRoom, sellerMessage, buyerMessage, seller, buyer);
        sellerChat.updateIsReadTrue();

        sendSmsToAdmin("관리자님 입금을 확인해주세요! \n판매글 ID는 " + chatRoom.getUsedBook().getId() + " 입니다.");

        return List.of(sellerChat.toChatMessageResponse());
    }

    private SellerChat saveAndSendMessage(ChatRoom chatRoom, String sellerMessage, String buyerMessage, Member seller, Member buyer) {
        SellerChat sellerChat = saveSellerChat(chatRoom, BOOK_PURCHASE_REQUEST_SELLER, sellerMessage);
        saveBuyerChat(chatRoom, BOOK_PURCHASE_REQUEST_BUYER, buyerMessage);

        sendNotification(seller, sellerMessage);
        sendNotification(buyer, buyerMessage);
        return sellerChat;
    }


}
