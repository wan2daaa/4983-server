package team.dankookie.server4983.chat.handler;

import static team.dankookie.server4983.chat.constant.ContentType.BOOK_PLACEMENT_COMPLETE;
import static team.dankookie.server4983.chat.constant.ContentType.BOOK_PLACEMENT_COMPLETE_BUYER;
import static team.dankookie.server4983.chat.constant.ContentType.BOOK_PLACEMENT_SET;
import static team.dankookie.server4983.chat.constant.ContentType.BOOK_PLACEMENT_SET_SELLER;
import static team.dankookie.server4983.chat.constant.ContentType.BOOK_PURCHASE_REQUEST;
import static team.dankookie.server4983.chat.constant.ContentType.BOOK_PURCHASE_REQUEST_BUYER;
import static team.dankookie.server4983.chat.constant.ContentType.BOOK_PURCHASE_REQUEST_SELLER;
import static team.dankookie.server4983.chat.constant.ContentType.BOOK_PURCHASE_START;
import static team.dankookie.server4983.chat.constant.ContentType.BOOK_PURCHASE_START_BUYER;
import static team.dankookie.server4983.chat.constant.ContentType.BOOK_PURCHASE_START_NOTIFY_BUYER;
import static team.dankookie.server4983.chat.constant.ContentType.BOOK_PURCHASE_START_NOTIFY_SELLER;
import static team.dankookie.server4983.chat.constant.ContentType.BOOK_PURCHASE_START_SELLER;
import static team.dankookie.server4983.chat.constant.ContentType.BOOK_PURCHASE_WARNING;
import static team.dankookie.server4983.chat.constant.ContentType.BOOK_SALE_REJECTION;
import static team.dankookie.server4983.chat.constant.ContentType.BOOK_SALE_REJECTION_BUYER;
import static team.dankookie.server4983.chat.constant.ContentType.BOOK_SALE_REJECTION_SELLER;
import static team.dankookie.server4983.chat.constant.ContentType.PAYMENT_CONFIRMATION_COMPLETE;
import static team.dankookie.server4983.chat.constant.ContentType.PAYMENT_CONFIRMATION_COMPLETE_BUYER;
import static team.dankookie.server4983.chat.constant.ContentType.PAYMENT_CONFIRMATION_COMPLETE_SELLER;
import static team.dankookie.server4983.chat.constant.ContentType.TRADE_COMPLETE;
import static team.dankookie.server4983.chat.constant.ContentType.TRADE_COMPLETE_BUYER;
import static team.dankookie.server4983.chat.constant.ContentType.TRADE_COMPLETE_SELLER;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import team.dankookie.server4983.book.domain.Locker;
import team.dankookie.server4983.book.repository.locker.LockerRepository;
import team.dankookie.server4983.chat.constant.ContentType;
import team.dankookie.server4983.chat.domain.BuyerChat;
import team.dankookie.server4983.chat.domain.ChatRoom;
import team.dankookie.server4983.chat.domain.SellerChat;
import team.dankookie.server4983.chat.dto.ChatMessageResponse;
import team.dankookie.server4983.chat.dto.ChatRequest;
import team.dankookie.server4983.chat.exception.ChatException;
import team.dankookie.server4983.chat.repository.BuyerChatRepository;
import team.dankookie.server4983.chat.repository.ChatRoomRepository;
import team.dankookie.server4983.chat.repository.SellerChatRepository;
import team.dankookie.server4983.fcm.dto.FcmTargetUserIdRequest;
import team.dankookie.server4983.fcm.service.FcmService;
import team.dankookie.server4983.member.domain.Member;
import team.dankookie.server4983.scheduler.repository.SchedulerRepository;
import team.dankookie.server4983.scheduler.service.SchedulerService;
import team.dankookie.server4983.sms.service.CoolSmsService;

@Component
@RequiredArgsConstructor
public class ChatLogicHandler {

    private final ChatRoomRepository chatRoomRepository;
    private final FcmService fcmService;
    private final LockerRepository lockerRepository;
    private final SchedulerService schedulerService;
    private final SchedulerRepository schedulerRepository;
    private final BuyerChatRepository buyerChatRepository;
    private final SellerChatRepository sellerChatRepository;
    private final CoolSmsService smsService;

    private enum UserRole {
        SELLER, BUYER, ALL
    }

    @Transactional
    public List<ChatMessageResponse> chatLogic(ChatRequest chatRequest) {

        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatRequest.getChatRoomId())
                .orElseThrow(() -> new ChatException("채팅방을 찾을 수 없습니다."));

        Member buyer = chatRoom.getBuyer();
        Member seller = chatRoom.getSeller();

        ifChattingAlreadyFinishedThrowError(chatRoom);

        switch (chatRequest.getContentType()) {
            case BOOK_PURCHASE_START -> { // SELLCHAT_1_1 판매자 구매 요청
                if (chatRoom.getInteractStep() >= 1) {
                    throw new ChatException("이미 거래 요청을 보냈습니다.");
                }
                String sellerMessage = getMessage(BOOK_PURCHASE_START, UserRole.SELLER, chatRoom);
                String buyerMessage = getMessage(BOOK_PURCHASE_START, UserRole.BUYER, chatRoom);

                saveSellerChat(chatRoom, BOOK_PURCHASE_START_SELLER, sellerMessage);
                BuyerChat buyerChat = saveBuyerChat(chatRoom, BOOK_PURCHASE_START_BUYER,
                        buyerMessage);

                sendNotification(seller, sellerMessage);
                sendNotification(buyer, buyerMessage);

                String sellerWarningMessage = getMessage(BOOK_PURCHASE_WARNING, UserRole.SELLER, chatRoom);
                String buyerWarningMessage = getMessage(BOOK_PURCHASE_WARNING, UserRole.BUYER, chatRoom);

                saveSellerChat(chatRoom, BOOK_PURCHASE_START_NOTIFY_SELLER, sellerWarningMessage);
                saveBuyerChat(chatRoom, BOOK_PURCHASE_START_NOTIFY_BUYER, buyerWarningMessage);

                sendNotification(seller, sellerWarningMessage);
                sendNotification(buyer, buyerWarningMessage);

                chatRoom.setInteractStep(1);

                return List.of();
            }

            case BOOK_PURCHASE_REQUEST -> { // SELLCHAT_2 판매자 구매 수락
                if (chatRoom.getInteractStep() >= 2) {
                    throw new ChatException("이미 거래 요청을 수락했습니다.");
                }
                String sellerMessage = getMessage(BOOK_PURCHASE_REQUEST, UserRole.SELLER, chatRoom);
                String buyerMessage = getMessage(BOOK_PURCHASE_REQUEST, UserRole.BUYER, chatRoom);

                SellerChat sellerChat = saveSellerChat(chatRoom, BOOK_PURCHASE_REQUEST_SELLER,
                        sellerMessage);
                BuyerChat buyerChat = saveBuyerChat(chatRoom, BOOK_PURCHASE_REQUEST_BUYER,
                        buyerMessage);

                sellerChat.updateIsReadTrue();

                sendNotification(seller, sellerMessage);
                sendNotification(buyer, buyerMessage);

                chatRoom.setInteractStep(2);

                smsService.sendAdminToSms(
                        "관리자님 입금을 확인해주세요! \n판매글 ID는 " + chatRoom.getUsedBook().getId() + " 입니다.");

//                schedulerService.setSchedulerAboutSetPlacement(chatRoom);
//                schedulerService.setSchedulerAboutNotDeposit(chatRoom,

                return List.of(sellerChat.toChatMessageResponse());
            }

            case BOOK_SALE_REJECTION -> { // SELLCHAT_1_2 거래 거절
                if (chatRoom.getInteractStep() >= 100) {
                    throw new ChatException("이미 거래를 거절했습니다.");
                }

                String sellerMessage = getMessage(BOOK_SALE_REJECTION, UserRole.SELLER, chatRoom);
                String buyerMessage = getMessage(BOOK_SALE_REJECTION, UserRole.BUYER, chatRoom);

                SellerChat sellerChat = saveSellerChat(chatRoom, BOOK_SALE_REJECTION_SELLER,
                        sellerMessage);
                BuyerChat buyerChat = saveBuyerChat(chatRoom, BOOK_SALE_REJECTION_BUYER,
                        buyerMessage);

                sellerChat.updateIsReadTrue();

                sendNotification(seller, sellerMessage);
                sendNotification(buyer, buyerMessage);

                chatRoom.setInteractStep(999);
//        schedulerService.setSchedulerAboutSetPlacement(chatRoom);
//        schedulerService.setSchedulerAboutNotDeposit(chatRoom);


                return List.of(sellerChat.toChatMessageResponse());
            }

            case PAYMENT_CONFIRMATION_COMPLETE -> { // SELLCHAT_4 입금 확인
                if (chatRoom.getInteractStep() >= 3) {
                    throw new ChatException("이미 입금 확인을 했습니다.");
                }

                String sellerMessage = getMessage(PAYMENT_CONFIRMATION_COMPLETE, UserRole.SELLER, chatRoom);
                String buyerMessage = getMessage(PAYMENT_CONFIRMATION_COMPLETE, UserRole.BUYER, chatRoom);

                saveSellerChat(chatRoom, PAYMENT_CONFIRMATION_COMPLETE_SELLER, sellerMessage);
                saveBuyerChat(chatRoom, PAYMENT_CONFIRMATION_COMPLETE_BUYER, buyerMessage);

                sendNotification(seller, sellerMessage);
                sendNotification(buyer, buyerMessage);

                chatRoom.setInteractStep(3);
//        schedulerRepository.deleteByChatRoomAndScheduleType(chatRoom, BUYER_CASE_1);
//        schedulerService.setSchedulerAboutNotReply(chatRoom);
                return List.of();
            }
            case BOOK_PLACEMENT_SET -> { // 서적 배치할 사물함 선택
                if (chatRoom.getInteractStep() >= 4) {
                    throw new ChatException("이미 사물함을 선택 하였습니다.");
                }

                Locker locker = lockerRepository.findByChatRoom(chatRoom)
                        .orElseThrow(() -> new ChatException("사물함을 찾을 수 없습니다."));

                String sellerMessage = getMessage(BOOK_PLACEMENT_SET, chatRoom, locker);
                saveSellerChat(chatRoom, BOOK_PLACEMENT_SET_SELLER, sellerMessage);
                sendNotification(seller, sellerMessage);

                chatRoom.setInteractStep(4);
//        schedulerRepository.deleteByChatRoomAndScheduleType(chatRoom, SELLER_CASE_2);
//        schedulerService.setSchedulerAboutNotComplete(chatRoom,
//            chatRoom.getUsedBook().getTradeAvailableDatetime(), locker);

                return List.of();
            }
            case BOOK_PLACEMENT_COMPLETE -> { // SELLCHAT_5 서적 배치 완료
                if (chatRoom.getInteractStep() >= 5) {
                    throw new ChatException("이미 서적 배치 완료 메시지를 구매자에게 보냈습니다.");
                }

                Locker locker = lockerRepository.findByChatRoom(chatRoom)
                        .orElseThrow(() -> new ChatException("서적을 배치할 사물함을 찾을 수 없습니다."));

                String buyerMessage = getMessage(BOOK_PLACEMENT_COMPLETE, chatRoom, locker);
                saveBuyerChat(chatRoom, BOOK_PLACEMENT_COMPLETE_BUYER, buyerMessage);

                sendNotification(buyer, buyerMessage);

                chatRoom.setInteractStep(5);

                return List.of();
//        schedulerService.setSchedulerAboutDontPressDone(chatRoom,
//            chatRoom.getUsedBook().getTradeAvailableDatetime());
//        schedulerRepository.deleteByChatRoomAndScheduleType(chatRoom, SELLER_CASE_3);
            }
            case TRADE_COMPLETE -> { // SELLCHAT_6 거래 완료
                if (chatRoom.getInteractStep() >= 6) {
                    throw new ChatException("이미 거래를 완료했습니다.");
                }
                releaseLocker(chatRoom);

                String message = getMessage(TRADE_COMPLETE, UserRole.ALL, chatRoom);

                saveSellerChat(chatRoom, TRADE_COMPLETE_SELLER, message);
                BuyerChat buyerChat = saveBuyerChat(chatRoom, TRADE_COMPLETE_BUYER, message);

                buyerChat.updateIsReadTrue();

                sendNotification(seller, message);
                sendNotification(buyer, message);

                chatRoom.setInteractStep(6);

                return List.of(buyerChat.toChatMessageResponse());
//        schedulerRepository.deleteByChatRoomAndScheduleType(chatRoom, BUYER_CASE_2);
//        sendNotification(seller, tradeCompleteSellerMessage);
            }

        }

        throw new ChatException("잘못된 데이터 요청입니다.");
    }

    private static void ifChattingAlreadyFinishedThrowError(ChatRoom chatRoom) {
        if (chatRoom.getInteractStep() == 999 || chatRoom.getInteractStep() == 1000 || chatRoom.getInteractStep() == 1001) {
            throw new ChatException("이미 종료된 거래입니다.");
        }
    }

    private String getMessage(ContentType contentType, UserRole userRole, ChatRoom chatRoom) {
        switch (contentType) {
            case BOOK_PURCHASE_START -> {
                switch (userRole) {
                    case SELLER -> {
                        return
                                String.format(
                                        "'%s' 님이 거래 요청을 보냈어요! \n" +
                                                "오늘 거래하러 갈래요?",
                                        chatRoom.getSeller().getNickname());

                    }
                    case BUYER -> {
                        return
                                String.format(
                                        "'%s' 님께 '%s' 서적 거래를 요청했습니다. \n\n" +
                                                "판매자의 응답을 기다려주세요. :)",
                                        chatRoom.getSeller().getNickname(), chatRoom.getUsedBook().getName());
                    }
                }
            }
            case BOOK_PURCHASE_WARNING -> {
                switch (userRole) {
                    case SELLER -> {
                        return
                                "전공서적은 거래 날짜와 시간에 맞게\n" +
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
                                        "\n";
                    }
                    case BUYER -> {
                        return
                                "전공서적은 거래 날짜와 시간 기준 “24시간 이내\"에 수령되어야 해요!\n" +
                                        "사물함 위치: 상경관 2층 GS25 옆 초록색 사물함을 찾아주세요!\n" +
                                        "\n" +
                                        "수령 후에는 “거래 완료\"를 클릭해야, 판매자에게 판매 금액이 송금되니, 꼭 클릭해 주시길 바랍니다!\n" +
                                        "\n" +
                                        "불가피하게 책을 수령하지 못할 경우, 아래의 번호/메일로 문의해주세요!\n" +
                                        "\n" +
                                        "휴대폰) 010-4487-3122\n" +
                                        "메일) 4983service@gmail.com\n";
                    }
                }
            }

            case BOOK_PURCHASE_REQUEST -> {
                switch (userRole) {
                    case SELLER -> {
                        return
                                "구매자에게 요청 수락 알림을 보냈습니다. \n" +
                                        "입금이 확인될 때 까지 기다려주세요. :)";
                    }

                    case BUYER -> {
                        return
                                String.format("'%s' 님께 '%s' 서적 거래를 요청을 수락했습니다. \n" +
                                                "아래 계좌정보로 결제금액을 송금하여 주십시오. \n" +
                                                "\n계좌번호:\n" +
                                                "카카오 7979-86-67501 (사고파삼) \n" +
                                                "결제 금액 : %d원", chatRoom.getSeller().getNickname(),
                                        chatRoom.getUsedBook().getName(), chatRoom.getUsedBook().getPrice());

                    }
                }
            }

            case BOOK_SALE_REJECTION -> {
                switch (userRole) {
                    case SELLER -> {
                        return
                                String.format("‘%s’ 님의 ‘%s’ 서적 거래 요청을 거절하셨습니다.\n" +
                                                "이후에도, 해당 서적 판매를 이어나가길 원하신다면, ‘네'를 클릭하여 거래 날짜를 수정해주세요.\n" +
                                                "\n" +
                                                "‘아니오’ 선택 시, 24시간 이내 해당 판매 게시글이 삭제됩니다.",
                                        chatRoom.getBuyer().getNickname(), chatRoom.getUsedBook().getName());
                    }
                    case BUYER -> {
                        return
                                String.format("아쉽게도 '%s' 님과의 서적 거래가 \n" +
                                        "이루어지지 못했습니다. \n", chatRoom.getSeller().getNickname());
                    }
                }
            }

            case PAYMENT_CONFIRMATION_COMPLETE -> {
                switch (userRole) {
                    case SELLER -> {
                        return
                                "입금이 확인되었습니다. \n" +
                                        "구매자가 “거래 완료\" 버튼을 클릭 후 \n" +
                                        "판매 금액이 자동으로 입금될 예정입니다.\n" +
                                        "\n" +
                                        "책을 넣을 사물함을 선택하고 비밀번호를 설정해주세요.";
                    }
                    case BUYER -> {
                        return
                                "입금이 확인되었습니다.\n" +
                                        "\n" +
                                        "거래날짜에 판매자가 사물함에 서적을 배치할 예정입니다.";
                    }
                }
            }

            case TRADE_COMPLETE -> {
                switch (userRole) {
                    case ALL -> {
                        return
                                "거래가 완료되었습니다.\n" +
                                        "이용해주셔서 감사합니다.\n" +
                                        "\n" +
                                        "-사고파삼-";
                    }
                }
            }
        }
        throw new RuntimeException("잘못된 타입입니다. seller , buyer 중 하나만 선택해주세요.");
    }

    private String getMessage(ContentType contentType, ChatRoom chatRoom, Locker locker) {
        switch (contentType) {
            case BOOK_PLACEMENT_SET -> {
                return
                        String.format("기입하셨던 거래 날짜에 맞게, 당일 내에 배치 해주시길 바랍니다. \n" +
                                        "\n" +
                                        "서적 배치 이후 완료 버튼을 눌러주세요.\n" +
                                        "\n" +
                                        "구매자가 배치된 서적을 수령한 후, “거래 완료” 버튼을 클릭하면 판매금액이 자동으로 입금됩니다.\n \n" +
                                        "사물함은 상경관 2층 GS25 편의점 옆 초록색 사물함을 찾아주세요:) \n \n" +
                                        "사물함 번호 : %s번 \n 거래 날짜 및 시간: %d월 %d일 %d:%d" +
                                        "\n",
                                locker.getLockerNumber(),
                                chatRoom.getUsedBook().getTradeAvailableDatetime().getMonthValue(),
                                chatRoom.getUsedBook().getTradeAvailableDatetime().getDayOfMonth(),
                                chatRoom.getUsedBook().getTradeAvailableDatetime().getHour(),
                                chatRoom.getUsedBook().getTradeAvailableDatetime().getMinute());
            }

            case BOOK_PLACEMENT_COMPLETE -> {
                return
                        String.format("서적 배치가 완료되었습니다.\n" +
                                        "금일 내 수령해주시길 바랍니다.\n" +
                                        "\n" +
                                        "“거래 완료\" 버튼을 눌러야, 판매자에게 판매금액이 입금되오니, 수령 후 버튼을 꼭 눌러주세요 \n" +
                                        "\n" +
                                        "사물함 번호: %s번\n" +
                                        "사물함 비밀번호: %s\n"
                                , locker.getLockerNumber()
                                , locker.getPassword());
            }
        }

        throw new RuntimeException("잘못된 타입입니다. seller , buyer 중 하나만 선택해주세요.");
    }

    private void sendNotification(Member buyer, String buyerMessage) {
        fcmService.sendChattingNotificationByToken(
                FcmTargetUserIdRequest.of(buyer.getId(), buyerMessage)
        );
    }

    private BuyerChat saveBuyerChat(ChatRoom chatRoom, ContentType contentType, String buyerMessage) {
        BuyerChat buyerChat = BuyerChat.buildBuyerChat(buyerMessage, contentType,
                chatRoom);
        buyerChatRepository.save(buyerChat);
        chatRoom.addBuyerChat(buyerChat);
        return buyerChat;
    }

    private SellerChat saveSellerChat(ChatRoom chatRoom, ContentType contentType,
                                      String sellerMessage) {
        SellerChat sellerChat = SellerChat.buildSellerChat(sellerMessage, contentType,
                chatRoom);
        sellerChatRepository.save(sellerChat);
        chatRoom.addSellerChat(sellerChat);
        return sellerChat;
    }

    private void releaseLocker(ChatRoom chatRoom) {
        Locker locker = lockerRepository.findByChatRoom(chatRoom)
                .orElseThrow(() -> new ChatException("사물함을 찾을 수 없습니다."));

        locker.releaseLocker();
    }

}
