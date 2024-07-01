package team.dankookie.server4983.chat.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team.dankookie.server4983.book.domain.Locker;
import team.dankookie.server4983.chat.domain.BuyerChat;
import team.dankookie.server4983.chat.domain.ChatRoom;
import team.dankookie.server4983.chat.domain.SellerChat;
import team.dankookie.server4983.chat.dto.ChatRequest;
import team.dankookie.server4983.chat.repository.BuyerChatRepository;
import team.dankookie.server4983.chat.repository.SellerChatRepository;

import java.util.List;

import static team.dankookie.server4983.chat.constant.ContentType.*;

@Deprecated
@Component
@RequiredArgsConstructor
public class ChatBotInteract {

    private final BuyerChatRepository buyerChatRepository;
    private final SellerChatRepository sellerChatRepository;

    public String purchaseBookStart(ChatRoom chatRoom) {
        String sellerMessage = String.format("'%s' 님이 거래 요청을 보냈어요! \n" +
                "오늘 거래하러 갈래요?", chatRoom.getSeller().getNickname());
        String buyerMessage = String.format("'%s' 님께 '%s' 서적 거래를 요청했습니다. \n\n" +
                " 판매자의 응답을 기다려주세요. :)", chatRoom.getSeller().getNickname(), chatRoom.getUsedBook().getName());

        SellerChat sellerChat = SellerChat.buildSellerChat(sellerMessage, BOOK_PURCHASE_START_SELLER, chatRoom);
        BuyerChat buyerChat = BuyerChat.buildBuyerChat(buyerMessage, BOOK_PURCHASE_START_BUYER, chatRoom);
        chatRoom.addSellerChat(sellerChat);
        chatRoom.addBuyerChat(buyerChat);
        chatRoom.setInteractStep(1);
        buyerChatRepository.save(buyerChat);
        sellerChatRepository.save(sellerChat);
        return sellerMessage;
    }

    public String purchaseBookWarning(ChatRoom chatRoom) {
        String sellerMessage = String.format("전공서적은 거래 날짜와 시간에 맞게\n" +
                "사물함에 배치되어야 해요!\n" +
                "\n" +
                "사물함 위치: 상경관 2층 GS25 옆 초록색 사물함을 찾아주세요!\n" +
                "\n" +
                "배치 후에는 “서적 배치 완료\"를 클릭해야, 구매자에게 사물함 정보가 전송되오니 꼭 클릭해 주시길 바랍니다!\n" +
                "\n" +
                "불가피하게 책을 배치 못할 경우, 아래의 번호/메일로 문의해 주세요!\n" +
                "\n" +
                "휴대폰) 010-4487-3122\n" +
                "메일) 4983service@gmail.com\n" +
                "\n");

        String buyerMessage = String.format("전공서적은 거래 날짜와 시간 기준 “24시간 이내\"에 수령되어야 해요!\n" +
                "사물함 위치: 상경관 2층 GS25 옆 초록색 사물함을 찾아주세요!\n" +
                "\n" +
                "수령 후에는 “거래 완료\"를 클릭해야, 판매자에게 판매 금액이 송금되니, 꼭 클릭해 주시길 바랍니다!\n" +
                "\n" +
                "불가피하게 책을 수령하지 못할 경우, 아래의 번호/메일로 문의해주세요!\n" +
                "\n" +
                "휴대폰) 010-4487-3122\n" +
                "메일) 4983service@gmail.com\n" +
                "\n");

        SellerChat sellerChat = SellerChat.buildSellerChat(sellerMessage, BOOK_PURCHASE_START_NOTIFY_SELLER, chatRoom);
        BuyerChat buyerChat = BuyerChat.buildBuyerChat(buyerMessage, BOOK_PURCHASE_START_NOTIFY_BUYER, chatRoom);
        chatRoom.addSellerChat(sellerChat);
        chatRoom.addBuyerChat(buyerChat);
        buyerChatRepository.save(buyerChat);
        sellerChatRepository.save(sellerChat);
        return buyerMessage;
    }

    public String purchaseBookApprove(ChatRoom chatRoom, String sellerNickName) {
        String sellerMessage = String.format("구매자에게 요청 수락 알림을 보냈습니다. \n" +
                "입금이 확인될 때 까지 기다려주세요. :)");
        String buyerMessage = String.format("\'%s\' 님께 \'%s\' 서적 거래를 요청을 수락했습니다. \n" +
                "아래 계좌정보로 결제금액을 송금하여 주십시오. \n" +
                "\n계좌번호:\n" +
                "우리 은행 1002-3597-18283 (사고팔삼)\n" +
                "결제 금액 : %d원", sellerNickName, chatRoom.getUsedBook().getName(), chatRoom.getUsedBook().getPrice());

        SellerChat sellerChat = SellerChat.buildSellerChat(sellerMessage, BOOK_PURCHASE_REQUEST_SELLER, chatRoom);
        BuyerChat buyerChat = BuyerChat.buildBuyerChat(buyerMessage, BOOK_PURCHASE_REQUEST_BUYER, chatRoom);
        chatRoom.addSellerChat(sellerChat);
        chatRoom.addBuyerChat(buyerChat);
        chatRoom.setInteractStep(2);
        buyerChatRepository.save(buyerChat);
        sellerChatRepository.save(sellerChat);
        return buyerMessage;
    }

    public String rejectPurchaseRequest(ChatRoom chatRoom, String userName, String sellerNickName) {
        String sellerMessage = String.format("‘%s’ 님의 ‘%s’ 서적 거래 요청을 거절하셨습니다.\n" +
                "이후에도, 해당 서적 판매를 이어나가길 원하신다면, ‘네'를 클릭하여 거래 날짜를 수정해주세요.\n" +
                "\n" +
                "‘아니오’ 선택 시, 24시간 이내 해당 판매 게시글이 삭제됩니다,", userName, chatRoom.getUsedBook().getName());
        String buyerMessage = String.format("아쉽게도 '%s' 님과의 서적 거래가 \n" +
                "이루어지지 못했습니다. \n", sellerNickName);

        SellerChat sellerChat = SellerChat.buildSellerChat(sellerMessage, BOOK_SALE_REJECTION_SELLER, chatRoom);
        BuyerChat buyerChat = BuyerChat.buildBuyerChat(buyerMessage, BOOK_SALE_REJECTION_BUYER, chatRoom);
        chatRoom.addSellerChat(sellerChat);
        chatRoom.addBuyerChat(buyerChat);
        chatRoom.setInteractStep(999);
        sellerChatRepository.save(sellerChat);
        buyerChatRepository.save(buyerChat);
        return buyerMessage;
    }

    public List<String> confirmDeposit(ChatRoom chatRoom) {
        String sellerMessage = String.format("입금이 확인되었습니다. \n" +
                "구매자가 “거래 완료\" 버튼을 클릭 후 \n" +
                "판매 금액이 자동으로 입금될 예정입니다.\n" +
                "\n" +
                "책을 넣을 사물함을 선택하고 비밀번호를 설정해주세요. \n");
        String buyerMessage = String.format("입금이 확인되었습니다.\n" +
                "\n" +
                "거래날짜에 판매자가 사물함에 서적을 배치할 예정입니다.\n");

        SellerChat sellerChat = SellerChat.buildSellerChat(sellerMessage, PAYMENT_CONFIRMATION_COMPLETE_SELLER, chatRoom);
        BuyerChat buyerChat = BuyerChat.buildBuyerChat(buyerMessage, PAYMENT_CONFIRMATION_COMPLETE_BUYER, chatRoom);
        chatRoom.addSellerChat(sellerChat);
        chatRoom.addBuyerChat(buyerChat);
        chatRoom.setInteractStep(3);
        sellerChatRepository.save(sellerChat);
        buyerChatRepository.save(buyerChat);
        return List.of(sellerMessage, buyerMessage);
    }

//    public String completeSelectLockAndPassword(ChatRoom chatRoom , ChatRequest request , Locker locker) {
//        String sellerMessage = String.format("기입하셨던 거래 날짜에 맞게, 당일 내에 배치 해주시길 바랍니다. \n" +
//                        "\n" +
//                        "서적 배치 이후 완료 버튼을 눌러주세요.\n" +
//                        "\n" +
//                        "구매자가 배치된 서적을 수령한 후, “거래 완료” 버튼을 클릭하면 판매금액이 자동으로 입금됩니다.\n \n" +
//                        "사물함은 상경관 2층 GS25 편의점 옆 초록색 사물함을 찾아주세요:) \n \n" +
//                        "사물함 번호 : %s번 \n 거래 날짜 및 시간: %d월 %d일 %d:%d" +
//                        "\n",
//                locker.getLockerNumber() , chatRoom.getUsedBook().getTradeAvailableDatetime().getMonthValue() , chatRoom.getUsedBook().getTradeAvailableDatetime().getDayOfMonth() , chatRoom.getUsedBook().getTradeAvailableDatetime().getHour() , chatRoom.getUsedBook().getTradeAvailableDatetime().getMinute());
//        String buyerMessage = String.format("서적 배치가 완료되었습니다.\n" +
//                "금일 내 수령해주시길 바랍니다.\n" +
//                "\n" +
//                "“거래 완료\" 버튼을 눌러야, 판매자에게 판매금액이 입금되오니, 수령 후 버튼을 꼭 눌러주세요 \n" +
//                "\n" +
//                "사물함 번호: %s번\n" +
//                "사물함 비밀번호: %s\n" , request.getData().get("lockerNumber") , request.getData().get("lockerPassword") );
//        SellerChat sellerChat = SellerChat.buildSellerChat(sellerMessage, BOOK_PLACEMENT_COMPLETE_SELLER, chatRoom);
//        BuyerChat buyerChat = BuyerChat.buildBuyerChat(buyerMessage, BOOK_PLACEMENT_COMPLETE_BUYER, chatRoom);
//        chatRoom.addSellerChat(sellerChat);
//        chatRoom.addBuyerChat(buyerChat);
//        chatRoom.setInteractStep(4);
//        sellerChatRepository.save(sellerChat);
//        buyerChatRepository.save(buyerChat);
//        return buyerMessage;
//    }

    public String completeTrade(ChatRoom chatRoom) {
        String message = String.format("거래가 완료되었습니다.\n" +
                "이용해주셔서 감사합니다.\n" +
                "\n" +
                "-사고파삼-\n");
        SellerChat sellerChat = SellerChat.buildSellerChat(message, TRADE_COMPLETE_SELLER, chatRoom);
        BuyerChat buyerChat = BuyerChat.buildBuyerChat(message, TRADE_COMPLETE_BUYER, chatRoom);
        chatRoom.addBuyerChat(buyerChat);
        chatRoom.addSellerChat(sellerChat);
        chatRoom.setInteractStep(6);
        sellerChatRepository.save(sellerChat);
        buyerChatRepository.save(buyerChat);
        return message;
    }

}
