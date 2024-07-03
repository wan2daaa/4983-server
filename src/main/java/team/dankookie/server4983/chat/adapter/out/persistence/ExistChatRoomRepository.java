package team.dankookie.server4983.chat.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team.dankookie.server4983.book.domain.UsedBook;
import team.dankookie.server4983.chat.application.port.out.ExistChatRoomPort;
import team.dankookie.server4983.chat.domain.ChatRoom;
import team.dankookie.server4983.member.domain.Member;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class ExistChatRoomRepository implements ExistChatRoomPort {
    private final ChatRoomRepository chatRoomRepository;

    @Override
    public Optional<ChatRoom> existsBySellerAndBuyerAndUsedBook(Member seller, Member buyer, UsedBook usedBook) {
        return chatRoomRepository.findBookBySellerAndBuyerAndBook(seller, buyer, usedBook);
    }
}
