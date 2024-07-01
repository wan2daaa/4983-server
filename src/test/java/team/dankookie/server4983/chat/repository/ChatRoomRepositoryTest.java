package team.dankookie.server4983.chat.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import team.dankookie.server4983.book.constant.BookStatus;
import team.dankookie.server4983.book.domain.UsedBook;
import team.dankookie.server4983.book.repository.usedBook.UsedBookRepository;
import team.dankookie.server4983.chat.constant.ContentType;
import team.dankookie.server4983.chat.domain.BuyerChat;
import team.dankookie.server4983.chat.domain.ChatRoom;
import team.dankookie.server4983.chat.domain.SellerChat;
import team.dankookie.server4983.chat.dto.ChatListResponse;
import team.dankookie.server4983.common.BaseRepositoryTest;
import team.dankookie.server4983.member.domain.Member;
import team.dankookie.server4983.member.fixture.MemberFixture;
import team.dankookie.server4983.member.repository.MemberRepository;

class ChatRoomRepositoryTest extends BaseRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    UsedBookRepository usedBookRepository;

    @Autowired
    ChatRoomRepository chatRoomRepository;

    @Autowired
    BuyerChatRepository buyerChatRepository;

    @Autowired
    SellerChatRepository sellerChatRepository;

    @Test
    void 유저의_채팅_리스트를_리턴한다() {
        //given
        String nickname = "testNickname";
        Member member = MemberFixture.createMemberByNickname(nickname);
        Member otherMember = MemberFixture.createMember();
        memberRepository.save(member);
        memberRepository.save(otherMember);

        UsedBook usedBook1 = UsedBook.builder()
                .bookStatus(BookStatus.SALE)
                .name("사회과학통계방법")
                .buyerMember(otherMember)
                .sellerMember(member)
                .price(1000)
                .tradeAvailableDatetime(LocalDateTime.now())
                .build();
        usedBookRepository.save(usedBook1);

        UsedBook usedBook2 = UsedBook.builder()
                .bookStatus(BookStatus.SALE)
                .name("사회과학통계방법")
                .buyerMember(member)
                .sellerMember(otherMember)
                .price(1000)
                .tradeAvailableDatetime(LocalDateTime.now())
                .build();
        usedBookRepository.save(usedBook2);


        ChatRoom chatRoom = ChatRoom.buildChatRoom(member, otherMember, usedBook1);
        chatRoomRepository.save(chatRoom);
        BuyerChat buyerChat = BuyerChat.buildBuyerChat("test", ContentType.BOOK_PURCHASE_START, chatRoom);
        buyerChatRepository.save(buyerChat);
        SellerChat sellerChat = SellerChat.buildSellerChat("test", ContentType.BOOK_PURCHASE_START, chatRoom);
        sellerChatRepository.save(sellerChat);

        //when
        List<ChatListResponse> chatListResponseList = chatRoomRepository.findByChatroomListWithNickname(nickname);

        //then
        assertThat(chatListResponseList).hasSize(1);
    }

    @Test
    void chatRoom에_구매자의_닉네임과_일치하는지_검증() throws Exception {
        String nickname = "testNickname";
        Member member = MemberFixture.createMemberByNickname(nickname);
        Member otherMember = MemberFixture.createMember();
        memberRepository.save(member);
        memberRepository.save(otherMember);

        UsedBook usedBook1 = UsedBook.builder()
                .bookStatus(BookStatus.SALE)
                .name("사회과학통계방법")
                .buyerMember(otherMember)
                .sellerMember(member)
                .price(1000)
                .tradeAvailableDatetime(LocalDateTime.now())
                .build();
        usedBookRepository.save(usedBook1);

        ChatRoom chatRoom = ChatRoom.buildChatRoom(member, otherMember, usedBook1);
        chatRoomRepository.save(chatRoom);

        boolean isExists = chatRoomRepository.existsByChatRoomIdAndBuyer_Nickname(1L, "testNickname");
    }

}