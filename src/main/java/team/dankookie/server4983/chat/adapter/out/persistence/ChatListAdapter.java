package team.dankookie.server4983.chat.adapter.out.persistence;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team.dankookie.server4983.chat.adapter.in.web.dto.ChatListResponse;
import team.dankookie.server4983.chat.adapter.in.web.dto.QChatListResponse;
import team.dankookie.server4983.chat.application.port.out.ChatListPort;

import java.util.Comparator;
import java.util.List;

import static team.dankookie.server4983.book.domain.QBookImage.bookImage;
import static team.dankookie.server4983.book.domain.QUsedBook.usedBook;
import static team.dankookie.server4983.chat.domain.QBuyerChat.buyerChat;
import static team.dankookie.server4983.chat.domain.QChatRoom.chatRoom;
import static team.dankookie.server4983.chat.domain.QSellerChat.sellerChat;
import static team.dankookie.server4983.member.domain.QMember.member;

@RequiredArgsConstructor
@Repository
public class ChatListAdapter implements ChatListPort {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ChatListResponse> getChatListWithMemberNickname(String nickname) {

        List<ChatListResponse> chatListResponseList = jpaQueryFactory
                .select(
                        new QChatListResponse(
                                chatRoom.chatRoomId,
                                usedBook.name,
                                sellerChat.message,
                                sellerChat.createdAt,
                                sellerChat.isRead,
                                getUsedBookMainImage())
                ).from(sellerChat)
                .innerJoin(chatRoom).on(sellerChat.chatRoom.eq(chatRoom))
                .innerJoin(usedBook).on(chatRoom.usedBook.eq(usedBook))
                .innerJoin(member).on(usedBook.sellerMember.eq(member))
                .where(
                        member.nickname.eq(nickname),
                        findLastSellerChat(nickname))
                .fetch();

        chatListResponseList.addAll(jpaQueryFactory
                .select(
                        new QChatListResponse(
                                chatRoom.chatRoomId,
                                usedBook.name,
                                buyerChat.message,
                                buyerChat.createdAt,
                                buyerChat.isRead,
                                getUsedBookMainImage())
                ).from(buyerChat)
                .innerJoin(chatRoom).on(buyerChat.chatRoom.eq(chatRoom))
                .innerJoin(usedBook).on(chatRoom.usedBook.eq(usedBook))
                .innerJoin(member).on(usedBook.buyerMember.eq(member))
                .where(
                        member.nickname.eq(nickname),
                        findLastBuyerChat(nickname))
                .fetch());

        chatListResponseList.sort(Comparator.comparing(
                ChatListResponse::createdAt,
                Comparator.nullsLast(Comparator.reverseOrder())
        ));

        return chatListResponseList;
    }

    private BooleanExpression findLastBuyerChat(String nickname) {
        return Expressions.list(buyerChat.chatRoom.chatRoomId, buyerChat.id).in(
                JPAExpressions.select(buyerChat.chatRoom.chatRoomId, buyerChat.id.max())
                        .from(buyerChat)
                        .innerJoin(chatRoom).on(buyerChat.chatRoom.eq(chatRoom))
                        .innerJoin(usedBook).on(chatRoom.usedBook.eq(usedBook))
                        .innerJoin(member).on(usedBook.buyerMember.eq(member))
                        .where(member.nickname.eq(nickname))
                        .groupBy(buyerChat.chatRoom.chatRoomId)
        );
    }

    private BooleanExpression findLastSellerChat(String nickname) {
        return Expressions.list(sellerChat.chatRoom.chatRoomId, sellerChat.id).in(
                JPAExpressions.select(sellerChat.chatRoom.chatRoomId, sellerChat.id.max())
                        .from(sellerChat)
                        .innerJoin(chatRoom).on(sellerChat.chatRoom.eq(chatRoom))
                        .innerJoin(usedBook).on(chatRoom.usedBook.eq(usedBook))
                        .innerJoin(member).on(usedBook.sellerMember.eq(member))
                        .where(member.nickname.eq(nickname))
                        .groupBy(sellerChat.chatRoom.chatRoomId)
        );
    }

    private JPQLQuery<String> getUsedBookMainImage() {
        return JPAExpressions
                .select(bookImage.imageUrl)
                .from(bookImage)
                .where(bookImage.usedBook.id.eq(usedBook.id)
                        .and(bookImage.id.eq(
                                JPAExpressions
                                        .select(bookImage.id.min())
                                        .from(bookImage)
                                        .where(bookImage.usedBook.id.eq(usedBook.id))
                        ))
                );
    }
}
