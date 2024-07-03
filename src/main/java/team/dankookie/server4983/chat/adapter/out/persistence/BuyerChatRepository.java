package team.dankookie.server4983.chat.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import team.dankookie.server4983.chat.domain.BuyerChat;

public interface BuyerChatRepository extends JpaRepository<BuyerChat, Long> {
}
