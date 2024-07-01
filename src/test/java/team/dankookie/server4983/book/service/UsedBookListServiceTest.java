package team.dankookie.server4983.book.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import team.dankookie.server4983.book.constant.BookStatus;
import team.dankookie.server4983.book.domain.UsedBook;
import team.dankookie.server4983.book.dto.UsedBookListResponse;
import team.dankookie.server4983.book.repository.bookImage.BookImageRepository;
import team.dankookie.server4983.book.repository.usedBook.UsedBookRepository;
import team.dankookie.server4983.common.BaseServiceTest;
import team.dankookie.server4983.member.domain.Member;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class UsedBookListServiceTest extends BaseServiceTest {

    @InjectMocks
    UsedBookListService usedBookListService;

    @Mock
    UsedBookRepository usedBookRepository;

    @Mock
    BookImageRepository bookImageRepository;

    @Test
    void 판매중인_책의_리스트를_리턴한다() {
        //given
        final String imageUrl = "imageUrl";
        final boolean canBuy = false;

        UsedBookListResponse usedBookListResponse1 = UsedBookListResponse.of(1L, imageUrl, BookStatus.SALE, "book", LocalDateTime.of(2023, 12, 12, 12, 30), LocalDateTime.of(2023, 12, 12, 12, 30), 10000);
        UsedBookListResponse usedBookListResponse2 = UsedBookListResponse.of(1L, imageUrl, BookStatus.SALE, "book", LocalDateTime.of(2023, 12, 12, 12, 30), LocalDateTime.of(2023, 12, 12, 12, 30), 10000);

        when(usedBookRepository.getUsedBookList(canBuy))
                .thenReturn(List.of(usedBookListResponse1, usedBookListResponse2));
        //when
        List<UsedBookListResponse> usedBookList = usedBookListService.getUsedBookList(canBuy);

        //then
        assertThat(usedBookList).hasSize(2);
        assertThat(usedBookList.get(0).imageUrl()).isEqualTo(imageUrl);
        assertThat(usedBookList.get(1).imageUrl()).isEqualTo(imageUrl);
    }

    private UsedBook createBook() {
        return UsedBook.builder()
                .name("book")
                .price(10000)
                .bookStatus(BookStatus.SALE)
                .tradeAvailableDatetime(LocalDateTime.now())
                .buyerMember(Member.builder().build())
                .build();
    }


}