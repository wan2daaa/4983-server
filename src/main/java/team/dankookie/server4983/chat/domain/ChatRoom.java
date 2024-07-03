package team.dankookie.server4983.chat.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import team.dankookie.server4983.book.domain.Locker;
import team.dankookie.server4983.book.domain.UsedBook;
import team.dankookie.server4983.common.domain.BaseEntity;
import team.dankookie.server4983.member.domain.Member;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Builder
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member seller;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BuyerChat> buyerChats = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SellerChat> sellerChats = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private UsedBook usedBook;

    @Setter
    @Builder.Default
    private int interactStep = 0; // ContentType 에 따른 1 ~ 5 단계

    @ColumnDefault("false")
    private boolean isFinished = false;

    @OneToOne(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private Locker locker;


    private ChatRoom(Long chatRoomId, Member buyer, Member seller, List<BuyerChat> buyerChats, List<SellerChat> sellerChats, UsedBook usedBook, int interactStep, boolean isFinished, Locker locker) {
        this.chatRoomId = chatRoomId;
        this.buyer = buyer;
        this.seller = seller;
        this.buyerChats = buyerChats;
        this.sellerChats = sellerChats;
        this.usedBook = usedBook;
        this.interactStep = interactStep;
        this.isFinished = isFinished;
        this.locker = locker;
    }

    private static ChatRoom createChatRoom(UsedBook usedBook, Member seller, Member buyer) {
        return new ChatRoom(null, buyer, seller,List.of(), List.of(), usedBook, 0, false, null);
    }

    public static ChatRoom create(UsedBook usedBook, Member seller, Member buyer) {
        ifBuyerAndSellerSameThenThrowException(seller, buyer);

        return ChatRoom.createChatRoom(usedBook, seller, buyer);

    }

    private static void ifBuyerAndSellerSameThenThrowException(Member seller, Member buyer) {
        if (seller.getId().equals(buyer.getId())) {
            throw new IllegalArgumentException("판매자와 구매자가 같을 수 없습니다.");
        }
    }

    public static ChatRoom buildChatRoom(Member buyer, Member seller, UsedBook usedBook) {
        return ChatRoom.builder().buyer(buyer).seller(seller).usedBook(usedBook).build();
    }

    public void addBuyerChat(BuyerChat chat) {
        if (this.getBuyerChats() == null) {
            this.buyerChats = new ArrayList<>();
            this.getBuyerChats().add(chat);
        } else {
            this.getBuyerChats().add(chat);
        }
    }

    public void addSellerChat(SellerChat chat) {
        if (this.getSellerChats() == null) {
            this.sellerChats = new ArrayList<>();
            this.getSellerChats().add(chat);
        } else {
            this.getSellerChats().add(chat);
        }
    }
}
