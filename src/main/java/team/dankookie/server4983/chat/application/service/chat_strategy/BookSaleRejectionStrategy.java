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

import static team.dankookie.server4983.chat.constant.ContentType.BOOK_SALE_REJECTION_BUYER;
import static team.dankookie.server4983.chat.constant.ContentType.BOOK_SALE_REJECTION_SELLER;

@Component
public class BookSaleRejectionStrategy extends ChatStrategy {

    public BookSaleRejectionStrategy(BuyerChatRepository buyerChatRepository, SellerChatRepository sellerChatRepository, FcmService fcmService, CoolSmsService smsService, LockerRepository lockerRepository) {
        super(buyerChatRepository, sellerChatRepository, fcmService, smsService, lockerRepository);
    }

    @Override
    public List<ChatMessageResponse> execute(ChatRoom chatRoom, ChatRequest chatRequest) {
        Member buyer = chatRoom.getBuyer();
        Member seller = chatRoom.getSeller();

        if (chatRoom.getInteractStep() >= 100) {
            throw new ChatException("이미 거래를 거절했습니다.");
        }

        String sellerMessage = String.format("""
                        ‘%s’ 님의 ‘%s’ 서적 거래 요청을 거절하셨습니다.
                        이후에도, 해당 서적 판매를 이어나가길 원하신다면, ‘네'를 클릭하여 거래 날짜를 수정해주세요.

                        ‘아니오’ 선택 시, 24시간 이내 해당 판매 게시글이 삭제됩니다.""",
                chatRoom.getBuyer().getNickname(), chatRoom.getUsedBook().getName());
        String buyerMessage = String.format("""
                아쉽게도 '%s' 님과의 서적 거래가\s
                이루어지지 못했습니다.\s
                """, chatRoom.getSeller().getNickname());

        SellerChat sellerChat = saveAndSendMessage(chatRoom, sellerMessage, buyerMessage, seller, buyer);
        sellerChat.updateIsReadTrue();

        chatRoom.setInteractStep(999);

        return List.of(sellerChat.toChatMessageResponse());
    }

    private SellerChat saveAndSendMessage(ChatRoom chatRoom, String sellerMessage, String buyerMessage, Member seller, Member buyer) {
        SellerChat sellerChat = saveSellerChat(chatRoom, BOOK_SALE_REJECTION_SELLER, sellerMessage);
        saveBuyerChat(chatRoom, BOOK_SALE_REJECTION_BUYER, buyerMessage);


        sendNotification(seller, sellerMessage);
        sendNotification(buyer, buyerMessage);
        return sellerChat;
    }
}
