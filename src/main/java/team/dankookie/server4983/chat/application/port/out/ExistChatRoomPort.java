package team.dankookie.server4983.chat.application.port.out;

import team.dankookie.server4983.book.domain.UsedBook;
import team.dankookie.server4983.chat.domain.ChatRoom;
import team.dankookie.server4983.member.domain.Member;

import java.util.Optional;

public interface ExistChatRoomPort {


    Optional<ChatRoom> existsBySellerAndBuyerAndUsedBook(Member seller, Member buyer, UsedBook usedBook);
}
