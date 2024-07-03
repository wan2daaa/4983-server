package team.dankookie.server4983.chat.application.service.chat_strategy;

import org.springframework.stereotype.Component;
import team.dankookie.server4983.book.domain.Locker;
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

import static team.dankookie.server4983.chat.constant.ContentType.BOOK_PLACEMENT_COMPLETE_BUYER;

@Component
public class BookPlacementCompleteStrategy extends ChatStrategy {
    public BookPlacementCompleteStrategy(BuyerChatRepository buyerChatRepository, SellerChatRepository sellerChatRepository, FcmService fcmService, CoolSmsService smsService, LockerRepository lockerRepository) {
        super(buyerChatRepository, sellerChatRepository, fcmService, smsService, lockerRepository);
    }

    @Override
    public List<ChatMessageResponse> execute(ChatRoom chatRoom, ChatRequest chatRequest) {
        Member buyer = chatRoom.getBuyer();

        if (chatRoom.getInteractStep() >= 5) {
            throw new ChatException("이미 서적 배치 완료 메시지를 구매자에게 보냈습니다.");
        }

        Locker locker = getLocker(chatRoom);

        String buyerMessage = String.format("""
                        서적 배치가 완료되었습니다.
                        금일 내 수령해주시길 바랍니다.

                        “거래 완료" 버튼을 눌러야, 판매자에게 판매금액이 입금되오니, 수령 후 버튼을 꼭 눌러주세요\s

                        사물함 번호: %s번
                        사물함 비밀번호: %s
                        """
                , locker.getLockerNumber()
                , locker.getPassword());
        saveAndSendMessage(chatRoom, buyerMessage, buyer);

        chatRoom.setInteractStep(5);

        return List.of();
    }


    private void saveAndSendMessage(ChatRoom chatRoom, String buyerMessage, Member buyer) {
        saveBuyerChat(chatRoom, BOOK_PLACEMENT_COMPLETE_BUYER, buyerMessage);

        sendNotification(buyer, buyerMessage);
    }
}
