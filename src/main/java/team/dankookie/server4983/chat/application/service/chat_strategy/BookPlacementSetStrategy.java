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

import static team.dankookie.server4983.chat.constant.ContentType.BOOK_PLACEMENT_SET_SELLER;

@Component
public class BookPlacementSetStrategy extends ChatStrategy {
    public BookPlacementSetStrategy(BuyerChatRepository buyerChatRepository, SellerChatRepository sellerChatRepository, FcmService fcmService, CoolSmsService smsService, LockerRepository lockerRepository) {
        super(buyerChatRepository, sellerChatRepository, fcmService, smsService, lockerRepository);
    }

    @Override
    public List<ChatMessageResponse> execute(ChatRoom chatRoom, ChatRequest chatRequest) {
        Member seller = chatRoom.getSeller();

        if (chatRoom.getInteractStep() >= 4) {
            throw new ChatException("이미 사물함을 선택 하였습니다.");
        }

        Locker locker = getLocker(chatRoom);

        String sellerMessage = String.format("""
                        기입하셨던 거래 날짜에 맞게, 당일 내에 배치 해주시길 바랍니다.\s

                        서적 배치 이후 완료 버튼을 눌러주세요.

                        구매자가 배치된 서적을 수령한 후, “거래 완료” 버튼을 클릭하면 판매금액이 자동으로 입금됩니다.
                        \s
                        사물함은 상경관 2층 GS25 편의점 옆 초록색 사물함을 찾아주세요:)\s
                        \s
                        사물함 번호 : %s번\s
                         거래 날짜 및 시간: %d월 %d일 %d:%d\

                        """,
                locker.getLockerNumber(),
                chatRoom.getUsedBook().getTradeAvailableDatetime().getMonthValue(),
                chatRoom.getUsedBook().getTradeAvailableDatetime().getDayOfMonth(),
                chatRoom.getUsedBook().getTradeAvailableDatetime().getHour(),
                chatRoom.getUsedBook().getTradeAvailableDatetime().getMinute());

        saveSellerChat(chatRoom, BOOK_PLACEMENT_SET_SELLER, sellerMessage);
        sendNotification(seller, sellerMessage);

        chatRoom.setInteractStep(4);

        return List.of();
    }
}
