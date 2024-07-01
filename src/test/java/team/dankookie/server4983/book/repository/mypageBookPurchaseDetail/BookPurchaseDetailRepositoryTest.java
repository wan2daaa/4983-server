package team.dankookie.server4983.book.repository.mypageBookPurchaseDetail;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import team.dankookie.server4983.book.constant.BookStatus;
import team.dankookie.server4983.book.constant.Department;
import team.dankookie.server4983.book.domain.UsedBook;
import team.dankookie.server4983.book.dto.UsedBookListResponse;
import team.dankookie.server4983.common.BaseRepositoryTest;
import team.dankookie.server4983.member.constant.AccountBank;
import team.dankookie.server4983.member.domain.Member;
import team.dankookie.server4983.member.repository.MemberRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class BookPurchaseDetailRepositoryTest extends BaseRepositoryTest {

    @Autowired
    BookPurchaseDetailRepository bookPurchaseDetailRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    void 거래완료인_서적을_리턴한다() {
        //given
        final BookStatus bookStatus = BookStatus.SOLD;

        Member member = Member.builder()
                .accountBank(AccountBank.K)
                .college(Department.DEPARTMENT_OF_LAW.getCollege())
                .department(Department.DEPARTMENT_OF_LAW)
                .accountHolder("홍길동")
                .phoneNumber("010-1234-5678")
                .password("password")
                .nickname("nickname")
                .yearOfAdmission(2022)
                .studentId("20151234")
                .accountNumber("123123123")
                .build();

        memberRepository.save(member);

        UsedBook bookPurchase = UsedBook.builder()
                .bookStatus(BookStatus.SOLD)
                .buyerMember(member)
                .sellerMember(member)
                .tradeAvailableDatetime(LocalDateTime.now())
                .price(10000)
                .name("책이름")
                .build();
        bookPurchaseDetailRepository.save(bookPurchase);

        //when
        List<UsedBookListResponse> myPageBookPurchaseDetailList = bookPurchaseDetailRepository.getMyPageBookPurchaseDetailList(bookStatus, member.getId());
        //then
        assertThat(myPageBookPurchaseDetailList).hasSize(1);
        assertThat(myPageBookPurchaseDetailList.get(0).bookStatus()).isEqualTo(BookStatus.SOLD);
    }

}
