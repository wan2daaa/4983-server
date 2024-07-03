package team.dankookie.server4983.chat.handler;

import static team.dankookie.server4983.chat.constant.ContentType.TRADE_STOP_REQUEST_AFTER_BOOK_PLACEMENT_BUYER;
import static team.dankookie.server4983.chat.constant.ContentType.TRADE_STOP_REQUEST_AFTER_BOOK_PLACEMENT_SELLER;
import static team.dankookie.server4983.chat.constant.ContentType.TRADE_STOP_REQUEST_AFTER_DEPOSIT_BUYER;
import static team.dankookie.server4983.chat.constant.ContentType.TRADE_STOP_REQUEST_AFTER_DEPOSIT_SELLER;
import static team.dankookie.server4983.chat.constant.ContentType.TRADE_STOP_REQUEST_BEFORE_DEPOSIT_BUYER;
import static team.dankookie.server4983.chat.constant.ContentType.TRADE_STOP_REQUEST_BEFORE_DEPOSIT_SELLER;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team.dankookie.server4983.chat.domain.BuyerChat;
import team.dankookie.server4983.chat.domain.ChatRoom;
import team.dankookie.server4983.chat.domain.SellerChat;
import team.dankookie.server4983.chat.adapter.out.persistence.BuyerChatRepository;
import team.dankookie.server4983.chat.adapter.out.persistence.SellerChatRepository;
import team.dankookie.server4983.fcm.dto.FcmTargetUserIdRequest;
import team.dankookie.server4983.fcm.service.FcmService;
import team.dankookie.server4983.member.domain.Member;

@Component
@RequiredArgsConstructor
public class ChatBotAdmin {

    private final BuyerChatRepository buyerChatRepository;
    private final SellerChatRepository sellerChatRepository;
    private final FcmService fcmService;

    public void tradeStopBySeller(ChatRoom chatRoom, Member seller, Member buyer) {
        int step = chatRoom.getInteractStep();

        switch (step) { // case1 거래하기 전
            case 1, 2 -> {
                String messageCase1_seller = "거래 중지 완료되었습니다.\n" +
                        "구매자분께 거래 중지 안내 드렸으며, 판매글의 거래 날짜와 시간을 수정해주시기 바랍니다.\n "
                        +
                        "미수정 시, 판매글이 삭제 될 수 있으니 참고해 주시기 바랍니다:) ";
                String messageCase1_buyer = String.format("'%s'님의 불가피한 사정으로 거래를 중단하셨습니다. \n" +
                                "거래가 이루어지지 못하였다는 점 양해 부탁드리며, 앞으로 원활한 거래를 위해 더욱 노력하겠습니다."
                        , seller.getNickname());
                SellerChat sellerChatCase1 = SellerChat.buildSellerChat(messageCase1_seller,
                        TRADE_STOP_REQUEST_BEFORE_DEPOSIT_SELLER, chatRoom);
                BuyerChat buyerChatCase1 = BuyerChat.buildBuyerChat(messageCase1_buyer,
                        TRADE_STOP_REQUEST_BEFORE_DEPOSIT_BUYER, chatRoom);
                chatRoom.addSellerChat(sellerChatCase1);
                chatRoom.addBuyerChat(buyerChatCase1);
                chatRoom.setInteractStep(1000);
                buyerChatRepository.save(buyerChatCase1);
                sellerChatRepository.save(sellerChatCase1);
                fcmService.sendChattingNotificationByToken(
                        FcmTargetUserIdRequest.of(seller.getId(), messageCase1_seller));
                fcmService.sendChattingNotificationByToken(
                        FcmTargetUserIdRequest.of(buyer.getId(), messageCase1_buyer));
            }
            case 3 -> { // case2 입금이 확인된 상태 or case3
                String messageCase2_seller = String.format("거래 중지 완료되었습니다.\n" +
                        "구매자분께 거래 중지 안내 드렸으며, 거래 보상 정책에 따라 판매금액은 구매자님의 계좌로 환불됩니다. \n"
                        +
                        "판매글의 거래 날짜와 시간을 수정해주시기 바랍니다.\n" +
                        "미수정 시, 판매글이 삭제 될 수 있으니 참고해 주시기 바랍니다:) ");
                String messageCase2_buyer = String.format("\'%s\'님의 불가피한 사정으로 거래를 중단하셨습니다.\n" +
                                "거래가 이루어지지 못하였다는 점 양해 부탁드리며, 앞으로 원활한 거래를 위해 더욱 노력하겠습니다.\n"
                                +
                                "입금해주신 판매 금액은 해당 계좌로 2~3일내 환불 조치가 이루어집니다.",
                        seller.getNickname());
                SellerChat sellerChatCase2 = SellerChat.buildSellerChat(messageCase2_seller,
                        TRADE_STOP_REQUEST_AFTER_DEPOSIT_SELLER, chatRoom);
                BuyerChat buyerChatCase2 = BuyerChat.buildBuyerChat(messageCase2_buyer,
                        TRADE_STOP_REQUEST_AFTER_DEPOSIT_BUYER, chatRoom);
                chatRoom.addSellerChat(sellerChatCase2);
                chatRoom.addBuyerChat(buyerChatCase2);
                chatRoom.setInteractStep(999);
                buyerChatRepository.save(buyerChatCase2);
                sellerChatRepository.save(sellerChatCase2);
                fcmService.sendChattingNotificationByToken(
                        FcmTargetUserIdRequest.of(seller.getId(), messageCase2_seller));
                fcmService.sendChattingNotificationByToken(
                        FcmTargetUserIdRequest.of(buyer.getId(), messageCase2_buyer));
            }
        }

    }

    public void tradeStopByBuyer(ChatRoom chatRoom, Member seller, Member buyer) {
        int step = chatRoom.getInteractStep();

        switch (step) { // case1 거래하기 전
            case 1, 2 -> {
                String messageCase1_seller = String.format("'%s' 님의 불가피한 사정으로 거래를 중단하셨습니다. \n" +
                                "거래가 이루어지지 못하였다는 점 양해 부탁드리며, 앞으로 원활한 거래를 위해 더욱 노력하겠습니다.\n"
                                +
                                "거래 날짜를 최신 날짜로 수정하면, 판매글이 상단에 노출되어 다음 구매자를 빠르게 구할 수 있어요!\n",
                        buyer.getNickname());
                String messageCase1_buyer = "거래 중지 완료되었습니다.\n" +
                        "판매자분께 거래 중지 안내 드렸습니다.\n" +
                        "앞으로의 원활한 거래를 위해 더욱 노력하겠습니다.";
                SellerChat sellerChatCase1 = SellerChat.buildSellerChat(messageCase1_seller,
                        TRADE_STOP_REQUEST_BEFORE_DEPOSIT_SELLER, chatRoom);
                BuyerChat buyerChatCase1 = BuyerChat.buildBuyerChat(messageCase1_buyer,
                        TRADE_STOP_REQUEST_BEFORE_DEPOSIT_BUYER, chatRoom);
                chatRoom.addSellerChat(sellerChatCase1);
                chatRoom.addBuyerChat(buyerChatCase1);
                chatRoom.setInteractStep(1001);
                buyerChatRepository.save(buyerChatCase1);
                sellerChatRepository.save(sellerChatCase1);
                fcmService.sendChattingNotificationByToken(
                        FcmTargetUserIdRequest.of(seller.getId(), messageCase1_seller));
                fcmService.sendChattingNotificationByToken(
                        FcmTargetUserIdRequest.of(buyer.getId(), messageCase1_buyer));
            }
            case 3 -> { // case2 입금이 확인된 상태 or case3
                String messageCase2_seller = String.format("'%s' 님의 불가피한 사정으로 거래를 중단하셨습니다.\n" +
                                "거래가 이루어지지 못하였다는 점 양해 부탁드리며, 앞으로 원활한 거래를 위해 더욱 노력하겠습니다.\n"
                                +
                                "거래 날짜를 최신 날짜로 수정하면, 판매글이 상단에 노출되어 다음 구매자를 빠르게 구할 수 있어요!\n"
                        , buyer.getNickname());
                String messageCase2_buyer =
                        "판매자에게 거래 중지 요청을 보냈습니다. 송금한 금액은 거래 중지일 기준 2~3일 후에 구매자님의 계좌로 환불됩니다.\n" +
                                "아래 환불 정책에 따라 환불금액을 확인해주세요!\n" +
                                "서적배치전: 100% 환불\n" +
                                "서적배치후: 70% 환불\n";
                SellerChat sellerChatCase2 = SellerChat.buildSellerChat(messageCase2_seller,
                        TRADE_STOP_REQUEST_AFTER_DEPOSIT_SELLER, chatRoom);
                BuyerChat buyerChatCase2 = BuyerChat.buildBuyerChat(messageCase2_buyer,
                        TRADE_STOP_REQUEST_AFTER_DEPOSIT_BUYER, chatRoom);
                chatRoom.addSellerChat(sellerChatCase2);
                chatRoom.addBuyerChat(buyerChatCase2);
                chatRoom.setInteractStep(999);
                buyerChatRepository.save(buyerChatCase2);
                sellerChatRepository.save(sellerChatCase2);
                fcmService.sendChattingNotificationByToken(
                        FcmTargetUserIdRequest.of(seller.getId(), messageCase2_seller));
                fcmService.sendChattingNotificationByToken(
                        FcmTargetUserIdRequest.of(buyer.getId(), messageCase2_buyer));
            }
            case 4 -> { // 서적 배치 완료
                String messageCase3_seller = String.format("'%s' 님의 불가피한 사정으로 거래를 중단하셨습니다.\n" +
                                "거래가 이루어지지 못하였다는 점 양해 부탁드리며, 앞으로 원활한 거래를 위해 더욱 노력하겠습니다.\n"
                                +
                                "판매자 거래 중지 보상 정책에 따라 거래 중지일 기준 2~3일후에 판매자님의 계좌로 거래보상금이 입금됩니다.\n"
                                +
                                "서적배치후 판매자거래보상: 판매금 30%"
                        , buyer.getNickname());
                String messageCase3_buyer =
                        "판매자에게 거래 중지 요청을 보냈습니다. 송금한 금액은 거래 중지일 기준 2~3일 후에 구매자님의 계좌로 환불됩니다.\n" +
                                "아래 환불 정책에 따라 환불금액을 확인해주세요!\n" +
                                "\n" +
                                "서적배치전: 100% 환불\n" +
                                "서적배치후: 70% 환불\n";
                SellerChat sellerChatCase3 = SellerChat.buildSellerChat(messageCase3_seller,
                        TRADE_STOP_REQUEST_AFTER_BOOK_PLACEMENT_SELLER, chatRoom);
                BuyerChat buyerChatCase3 = BuyerChat.buildBuyerChat(messageCase3_buyer,
                        TRADE_STOP_REQUEST_AFTER_BOOK_PLACEMENT_BUYER, chatRoom);
                chatRoom.addSellerChat(sellerChatCase3);
                chatRoom.addBuyerChat(buyerChatCase3);
                chatRoom.setInteractStep(999);
                buyerChatRepository.save(buyerChatCase3);
                sellerChatRepository.save(sellerChatCase3);
                fcmService.sendChattingNotificationByToken(
                        FcmTargetUserIdRequest.of(seller.getId(), messageCase3_seller));
                fcmService.sendChattingNotificationByToken(
                        FcmTargetUserIdRequest.of(buyer.getId(), messageCase3_buyer));


            }
        }
    }
}
