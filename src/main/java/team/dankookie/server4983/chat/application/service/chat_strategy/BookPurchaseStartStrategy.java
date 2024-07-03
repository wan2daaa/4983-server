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

import static team.dankookie.server4983.chat.constant.ContentType.*;

@Component
public class BookPurchaseStartStrategy extends ChatStrategy {

    public BookPurchaseStartStrategy(BuyerChatRepository buyerChatRepository, SellerChatRepository sellerChatRepository, FcmService fcmService, CoolSmsService smsService, LockerRepository lockerRepository) {
        super(buyerChatRepository, sellerChatRepository, fcmService, smsService, lockerRepository);
    }

    @Override
    public List<ChatMessageResponse> execute(ChatRoom chatRoom, ChatRequest chatRequest) {
        Member buyer = chatRoom.getBuyer();
        Member seller = chatRoom.getSeller();

        if (chatRoom.getInteractStep() >= 1) {
            throw new ChatException("이미 거래 요청을 보냈습니다.");
        }

        String sellerMessage = String.format("""
                        '%s' 님이 거래 요청을 보냈어요!\s
                        오늘 거래하러 갈래요?""",
                chatRoom.getSeller().getNickname());
        String buyerMessage = String.format("""
                        '%s' 님께 '%s' 서적 거래를 요청했습니다.\s

                        판매자의 응답을 기다려주세요. :)""",
                chatRoom.getSeller().getNickname(), chatRoom.getUsedBook().getName());
        saveAndSendFirstMessage(sellerMessage, buyerMessage, chatRoom, seller, buyer);


        String sellerWarningMessage = """
                전공서적은 거래 날짜와 시간에 맞게
                사물함에 배치되어야 해요!

                사물함 위치: 상경관 2층 GS25 옆 초록색 사물함을 찾아주세요!

                배치 후에는 “서적 배치 완료"를 클릭해야, 구매자에게 사물함 정보가 전송되오니 꼭 클릭해 주시길 바랍니다!

                불가피하게 책을 배치 못할 경우, 아래의 번호/메일로 문의해 주세요!

                휴대폰) 010-4487-3122
                메일) 4983service@gmail.com

                """;
        String buyerWarningMessage = """
                전공서적은 거래 날짜와 시간 기준 “24시간 이내"에 수령되어야 해요!
                사물함 위치: 상경관 2층 GS25 옆 초록색 사물함을 찾아주세요!

                수령 후에는 “거래 완료"를 클릭해야, 판매자에게 판매 금액이 송금되니, 꼭 클릭해 주시길 바랍니다!

                불가피하게 책을 수령하지 못할 경우, 아래의 번호/메일로 문의해주세요!

                휴대폰) 010-4487-3122
                메일) 4983service@gmail.com
                """;
        saveAndSendWarningMessage(sellerWarningMessage, buyerWarningMessage, chatRoom, seller, buyer);

        chatRoom.setInteractStep(1);

        return List.of();
    }

    private void saveAndSendFirstMessage(String sellerMessage, String buyerMessage, ChatRoom chatRoom, Member seller, Member buyer) {
        saveSellerChat(chatRoom, BOOK_PURCHASE_START_SELLER, sellerMessage);
        saveBuyerChat(chatRoom, BOOK_PURCHASE_START_BUYER, buyerMessage);

        sendNotification(seller, sellerMessage);
        sendNotification(buyer, buyerMessage);
    }

    private void saveAndSendWarningMessage(String sellerMessage, String buyerMessage, ChatRoom chatRoom, Member seller, Member buyer) {
        saveSellerChat(chatRoom, BOOK_PURCHASE_START_NOTIFY_SELLER, sellerMessage);
        saveBuyerChat(chatRoom, BOOK_PURCHASE_START_NOTIFY_BUYER, buyerMessage);

        sendNotification(seller, sellerMessage);
        sendNotification(buyer, buyerMessage);
    }

}
