package team.dankookie.server4983.chat.application.port.out;

import team.dankookie.server4983.book.domain.UsedBook;

public interface GetUsedBookPort {
    UsedBook getUsedBookById(Long usedBookId);
}
