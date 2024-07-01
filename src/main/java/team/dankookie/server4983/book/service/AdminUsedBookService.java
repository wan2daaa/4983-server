package team.dankookie.server4983.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.dankookie.server4983.book.constant.BookStatus;
import team.dankookie.server4983.book.domain.UsedBook;
import team.dankookie.server4983.book.dto.AdminUsedBookListResponse;
import team.dankookie.server4983.book.repository.usedBook.UsedBookRepository;

@RequiredArgsConstructor
@Service
public class AdminUsedBookService {

    private final UsedBookRepository usedBookRepository;

    public Page<AdminUsedBookListResponse> getUsedBook(Pageable pageable, String searchKeyword, BookStatus bookStatus) {

        return usedBookRepository.getAdminUsedBookList(pageable, searchKeyword, bookStatus);
    }

    @Transactional
    public void updateBookStatus(Long id, BookStatus bookStatus) {
        UsedBook usedBook = usedBookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));
        usedBook.setBookStatus(bookStatus);
    }
}