package team.dankookie.server4983.book.repository.mypageBookSalesDetail;

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

class BookSalesDetailRepositoryTest extends BaseRepositoryTest {

    @Autowired
    BookSalesDetailRepository bookSalesDetailRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    void 판매중인_서적을_리턴한다() {
        //given
        final boolean canBuy = true;

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

        UsedBook bookSalesOn = UsedBook.builder()
                .bookStatus(BookStatus.SALE)
                .sellerMember(member)
                .tradeAvailableDatetime(LocalDateTime.now())
                .price(10000)
                .name("책이름")
                .build();

        bookSalesDetailRepository.save(bookSalesOn);

        //when
        List<UsedBookListResponse> mypageBookSalesDetailList = bookSalesDetailRepository.getMyPageBookSalesDetailList(canBuy, member.getId());
        //then
        assertThat(mypageBookSalesDetailList).hasSize(1);
        assertThat(mypageBookSalesDetailList.get(0).bookStatus()).isEqualTo(BookStatus.SALE);
    }

    @Test
    void 거래완료인_서적을_리턴한다() {
        //given
        final boolean canBuy = false;

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

        UsedBook bookSalesOn = UsedBook.builder()
                .bookStatus(BookStatus.SOLD)
                .sellerMember(member)
                .tradeAvailableDatetime(LocalDateTime.now())
                .price(10000)
                .name("책이름")
                .build();

        bookSalesDetailRepository.save(bookSalesOn);

        //when
        List<UsedBookListResponse> mypageBookSalesDetailList = bookSalesDetailRepository.getMyPageBookSalesDetailList(canBuy, member.getId());
        //then
        assertThat(mypageBookSalesDetailList).hasSize(1);
        assertThat(mypageBookSalesDetailList.get(0).bookStatus()).isEqualTo(BookStatus.SOLD);
    }
}