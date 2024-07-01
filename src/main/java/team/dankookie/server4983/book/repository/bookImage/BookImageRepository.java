package team.dankookie.server4983.book.repository.bookImage;

import org.springframework.data.jpa.repository.JpaRepository;
import team.dankookie.server4983.book.domain.BookImage;
import team.dankookie.server4983.book.domain.UsedBook;

import java.util.List;

public interface BookImageRepository extends JpaRepository<BookImage, Long>, BookImageRepositoryCustom {
    List<BookImage> findByUsedBook(UsedBook usedBook);

    long deleteBookImageByUsedBookAndImageUrl(UsedBook usedBook, String imageUrl);
}
