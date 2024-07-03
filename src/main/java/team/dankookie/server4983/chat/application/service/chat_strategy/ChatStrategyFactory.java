package team.dankookie.server4983.chat.application.service.chat_strategy;

import org.springframework.stereotype.Component;
import team.dankookie.server4983.chat.constant.ContentType;

import java.util.HashMap;
import java.util.Map;

@Component
public class ChatStrategyFactory {

    private final Map<ContentType, ChatStrategy> strategies = new HashMap<>();

    public ChatStrategyFactory(
            BookPurchaseStartStrategy bookPurchaseStartStrategy,
            BookPurchaseRequestStrategy bookPurchaseRequestStrategy,
            BookSaleRejectionStrategy bookSaleRejectionStrategy,
            PaymentConfirmationCompleteStrategy paymentConfirmationCompleteStrategy,
            BookPlacementSetStrategy bookPlacementSetStrategy,
            BookPlacementCompleteStrategy bookPlacementCompleteStrategy,
            TradeCompleteStrategy tradeCompleteStrategy
    ) {
        strategies.put(ContentType.BOOK_PURCHASE_START, bookPurchaseStartStrategy);
        strategies.put(ContentType.BOOK_PURCHASE_REQUEST, bookPurchaseRequestStrategy);
        strategies.put(ContentType.BOOK_SALE_REJECTION, bookSaleRejectionStrategy);
        strategies.put(ContentType.PAYMENT_CONFIRMATION_COMPLETE, paymentConfirmationCompleteStrategy);
        strategies.put(ContentType.BOOK_PLACEMENT_SET, bookPlacementSetStrategy);
        strategies.put(ContentType.BOOK_PLACEMENT_COMPLETE, bookPlacementCompleteStrategy);
        strategies.put(ContentType.TRADE_COMPLETE, tradeCompleteStrategy);
    }

    public ChatStrategy getStrategy(ContentType contentType) {
        return strategies.get(contentType);
    }
}
