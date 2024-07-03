package team.dankookie.server4983.common.init;

import static team.dankookie.server4983.chat.domain.ChatRoom.buildChatRoom;

import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import team.dankookie.server4983.book.constant.BookStatus;
import team.dankookie.server4983.book.constant.College;
import team.dankookie.server4983.book.constant.Department;
import team.dankookie.server4983.book.domain.BookImage;
import team.dankookie.server4983.book.domain.UsedBook;
import team.dankookie.server4983.book.repository.bookImage.BookImageRepository;
import team.dankookie.server4983.book.repository.usedBook.UsedBookRepository;
import team.dankookie.server4983.chat.domain.ChatRoom;
import team.dankookie.server4983.chat.adapter.out.persistence.ChatRoomRepository;
import team.dankookie.server4983.member.constant.AccountBank;
import team.dankookie.server4983.member.constant.UserRole;
import team.dankookie.server4983.member.domain.Member;
import team.dankookie.server4983.member.repository.MemberRepository;

@RequiredArgsConstructor
@Service
public class PostConstructService {
    private final ChatRoomRepository chatRoomRepository;
    private final BookImageRepository bookImageRepository;
    private final UsedBookRepository usedBookRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @PostConstruct
    public void init() {
        Member testMember1 = Member.builder()
                .studentId("202023604")
                .college(Department.COMPUTER.getCollege())
                .department(Department.COMPUTER)
                .yearOfAdmission(2020)
                .nickname("닉네임1")
                .password(passwordEncoder.encode("1234"))
                .phoneNumber("01073352306")
                .accountHolder("박재완")
                .accountBank(AccountBank.KB)
                .accountNumber("12500104097324")
                .marketingAgree(true)
                .imageUrl("https://4983-s3.s3.ap-northeast-2.amazonaws.com/ba760a510066638ed5cc7e1bc3b38f1e.png")
                .role(UserRole.USER)
                .build();

        Member testMember2 = Member.builder()
                .studentId("test")
                .college(Department.COMPUTER.getCollege())
                .department(Department.COMPUTER)
                .yearOfAdmission(2020)
                .nickname("닉네임2")
                .password(passwordEncoder.encode("1234"))
                .phoneNumber("01073352306")
                .accountHolder("박박박")
                .accountBank(AccountBank.KB)
                .accountNumber("12500104097324")
                .marketingAgree(true)
                .imageUrl("https://4983-s3.s3.ap-northeast-2.amazonaws.com/ba760a510066638ed5cc7e1bc3b38f1e.png")
                .role(UserRole.USER)
                .build();

        Member testAdmin = Member.builder()
                .studentId("admin")
                .college(Department.COMPUTER.getCollege())
                .department(Department.COMPUTER)
                .yearOfAdmission(2020)
                .nickname("admin")
                .password(passwordEncoder.encode("1234"))
                .phoneNumber("01073352306")
                .accountHolder("박박박")
                .accountBank(AccountBank.KB)
                .accountNumber("12500104097324")
                .marketingAgree(true)
                .imageUrl("https://4983-s3.s3.ap-northeast-2.amazonaws.com/ba760a510066638ed5cc7e1bc3b38f1e.png")
                .role(UserRole.ADMIN)
                .build();

        Member testAdmin2 = Member.builder()
                .studentId("4983admin")
                .college(Department.COMPUTER.getCollege())
                .department(Department.COMPUTER)
                .yearOfAdmission(2020)
                .nickname("4983admin")
                .password(passwordEncoder.encode("tkrhvktka"))
                .phoneNumber("01000000000")
                .accountHolder("admin")
                .accountBank(AccountBank.KB)
                .accountNumber("admin")
                .marketingAgree(true)
                .imageUrl("https://4983-s3.s3.ap-northeast-2.amazonaws.com/ba760a510066638ed5cc7e1bc3b38f1e.png")
                .role(UserRole.ADMIN)
                .build();

        memberRepository.save(testMember1);
        memberRepository.save(testMember2);
        memberRepository.save(testAdmin);
        memberRepository.save(testAdmin2);


        UsedBook usedBook1 = UsedBook.builder()
                .bookStatus(BookStatus.SALE)
                .name("사회과학통계방법")
                .tradeAvailableDatetime(LocalDateTime.of(2023, 7, 29, 12, 30, 30))
                .price(13000)
                .isUnderlinedOrWrite(false)
                .isCoverDamaged(true)
                .isDiscolorationAndDamage(true)
                .department(Department.COMMUNICATION_DESIGN)
                .college(College.MUSIC_AND_ARTS)
                .publisher("한국도서출판")
                .sellerMember(testMember1)
                .build();

        UsedBook usedBook2 = UsedBook.builder()
                .bookStatus(BookStatus.SALE)
                .name("생명과학 길라잡이")
                .tradeAvailableDatetime(LocalDateTime.of(2023, 8, 9, 12, 30, 30))
                .price(9000)
                .isUnderlinedOrWrite(true)
                .isCoverDamaged(true)
                .isDiscolorationAndDamage(true)
                .department(Department.COMMUNICATION_DESIGN)
                .college(College.MUSIC_AND_ARTS)
                .publisher("한국도서출판")
                .sellerMember(testMember1)
                .build();

        UsedBook usedBook3 = UsedBook.builder()
                .bookStatus(BookStatus.SALE)
                .name("경영학 원론")
                .tradeAvailableDatetime(LocalDateTime.of(2023, 7, 29, 12, 30, 30))
                .price(9000)
                .isUnderlinedOrWrite(false)
                .isCoverDamaged(false)
                .isDiscolorationAndDamage(false)
                .department(Department.BUSINESS)
                .college(College.BUSINESS_AND_ECONOMICS)
                .publisher("한국도서출판")
                .sellerMember(testMember2)
                .build();

        UsedBook usedBook4 = UsedBook.builder()
                .bookStatus(BookStatus.TRADE)
                .name("컴퓨터 개론")
                .tradeAvailableDatetime(LocalDateTime.of(2023, 7, 29, 11, 11, 11))
                .price(14000)
                .isUnderlinedOrWrite(false)
                .isCoverDamaged(false)
                .isDiscolorationAndDamage(false)
                .department(Department.BUSINESS)
                .college(College.BUSINESS_AND_ECONOMICS)
                .publisher("한국도서출판")
                .sellerMember(testMember2)
                .build();

        UsedBook usedBook5 = UsedBook.builder()
                .bookStatus(BookStatus.SOLD)
                .name("일반 화학")
                .tradeAvailableDatetime(LocalDateTime.of(2023, 7, 29, 12, 30, 00))
                .price(7000)
                .isUnderlinedOrWrite(true)
                .isCoverDamaged(true)
                .isDiscolorationAndDamage(true)
                .department(Department.CHEMICAL)
                .college(College.ENGINEERING)
                .publisher("한국도서출판")
                .sellerMember(testMember2)
                .build();

        UsedBook usedBook6 = UsedBook.builder()
                .bookStatus(BookStatus.SOLD)
                .name("일반 화학2")
                .tradeAvailableDatetime(LocalDateTime.of(2023, 7, 29, 13, 00, 00))
                .price(70002)
                .isUnderlinedOrWrite(true)
                .isCoverDamaged(true)
                .isDiscolorationAndDamage(true)
                .department(Department.CHEMICAL)
                .college(College.ENGINEERING)
                .publisher("한국도서출판")
                .sellerMember(testMember2)
                .build();
        UsedBook usedBook7 = UsedBook.builder()
                .bookStatus(BookStatus.SOLD)
                .name("일반 화학3")
                .tradeAvailableDatetime(LocalDateTime.of(2023, 7, 29, 11, 0))
                .price(100)
                .isUnderlinedOrWrite(true)
                .isCoverDamaged(true)
                .isDiscolorationAndDamage(true)
                .department(Department.CHEMICAL)
                .college(College.ENGINEERING)
                .publisher("한국도서출판")
                .sellerMember(testMember2)
                .build();
        UsedBook usedBook8 = UsedBook.builder()
                .bookStatus(BookStatus.SOLD)
                .name("일반 화학4")
                .tradeAvailableDatetime(LocalDateTime.of(2023, 7, 29, 15, 0))
                .price(100)
                .isUnderlinedOrWrite(true)
                .isCoverDamaged(true)
                .isDiscolorationAndDamage(true)
                .department(Department.CHEMICAL)
                .college(College.ENGINEERING)
                .publisher("한국도서출판")
                .sellerMember(testMember2)
                .build();
        UsedBook usedBook9 = UsedBook.builder()
                .bookStatus(BookStatus.SOLD)
                .name("일반 화학5")
                .tradeAvailableDatetime(LocalDateTime.of(2023, 7, 29, 8, 0))
                .price(100)
                .isUnderlinedOrWrite(true)
                .isCoverDamaged(true)
                .isDiscolorationAndDamage(true)
                .department(Department.CHEMICAL)
                .college(College.ENGINEERING)
                .publisher("한국도서출판")
                .sellerMember(testMember2)
                .build();
        UsedBook usedBook10 = UsedBook.builder()
                .bookStatus(BookStatus.SOLD)
                .name("일반 화학6")
                .tradeAvailableDatetime(LocalDateTime.of(2023, 7, 29, 6, 0))
                .price(100)
                .isUnderlinedOrWrite(true)
                .isCoverDamaged(true)
                .isDiscolorationAndDamage(true)
                .department(Department.CHEMICAL)
                .college(College.ENGINEERING)
                .publisher("한국도서출판")
                .sellerMember(testMember2)
                .build();

        usedBookRepository.save(usedBook1);
        usedBookRepository.save(usedBook2);
        usedBookRepository.save(usedBook3);
        usedBookRepository.save(usedBook4);
        usedBookRepository.save(usedBook5);
        usedBookRepository.save(usedBook6);
        usedBookRepository.save(usedBook7);
        usedBookRepository.save(usedBook8);
        usedBookRepository.save(usedBook9);
        usedBookRepository.save(usedBook10);

        bookImageRepository.save(BookImage.builder()
                .usedBook(usedBook1)
                .imageUrl("https://4983-s3.s3.ap-northeast-2.amazonaws.com/0ef2f95b-65b5-4345-bee4-cd9339108302")
                .build());
        bookImageRepository.save(BookImage.builder()
                .usedBook(usedBook1)
                .imageUrl("https://4983-s3.s3.ap-northeast-2.amazonaws.com/0ef2f95b-65b5-4345-bee4-cd9339108302")
                .build());
        bookImageRepository.save(BookImage.builder()
                .usedBook(usedBook1)
                .imageUrl("https://4983-s3.s3.ap-northeast-2.amazonaws.com/0ef2f95b-65b5-4345-bee4-cd9339108302")
                .build());
        bookImageRepository.save(BookImage.builder()
                .usedBook(usedBook1)
                .imageUrl("https://4983-s3.s3.ap-northeast-2.amazonaws.com/0ef2f95b-65b5-4345-bee4-cd9339108302")
                .build());
        bookImageRepository.save(BookImage.builder()
                .usedBook(usedBook2)
                .imageUrl("https://4983-s3.s3.ap-northeast-2.amazonaws.com/0ef2f95b-65b5-4345-bee4-cd9339108302")
                .build());
        bookImageRepository.save(BookImage.builder()
                .usedBook(usedBook3)
                .imageUrl("https://4983-s3.s3.ap-northeast-2.amazonaws.com/0ef2f95b-65b5-4345-bee4-cd9339108302")
                .build());
        bookImageRepository.save(BookImage.builder()
                .usedBook(usedBook4)
                .imageUrl("https://4983-s3.s3.ap-northeast-2.amazonaws.com/0ef2f95b-65b5-4345-bee4-cd9339108302")
                .build());
        bookImageRepository.save(BookImage.builder()
                .usedBook(usedBook5)
                .imageUrl("https://4983-s3.s3.ap-northeast-2.amazonaws.com/0ef2f95b-65b5-4345-bee4-cd9339108302")
                .build());
        bookImageRepository.save(BookImage.builder()
                .usedBook(usedBook6)
                .imageUrl("https://4983-s3.s3.ap-northeast-2.amazonaws.com/0ef2f95b-65b5-4345-bee4-cd9339108302")
                .build());
        bookImageRepository.save(BookImage.builder()
                .usedBook(usedBook7)
                .imageUrl("https://4983-s3.s3.ap-northeast-2.amazonaws.com/0ef2f95b-65b5-4345-bee4-cd9339108302")
                .build());
        bookImageRepository.save(BookImage.builder()
                .usedBook(usedBook8)
                .imageUrl("https://4983-s3.s3.ap-northeast-2.amazonaws.com/0ef2f95b-65b5-4345-bee4-cd9339108302")
                .build());
        bookImageRepository.save(BookImage.builder()
                .usedBook(usedBook9)
                .imageUrl("https://4983-s3.s3.ap-northeast-2.amazonaws.com/0ef2f95b-65b5-4345-bee4-cd9339108302")
                .build());
        bookImageRepository.save(BookImage.builder()
                .usedBook(usedBook10)
                .imageUrl("https://4983-s3.s3.ap-northeast-2.amazonaws.com/0ef2f95b-65b5-4345-bee4-cd9339108302")
                .build());


        ChatRoom chatRoom_1 = buildChatRoom(testMember1, testMember2, usedBook1);
        ChatRoom chatRoom_2 = buildChatRoom(testMember2, testMember1, usedBook2);
        chatRoomRepository.save(chatRoom_1);
        chatRoomRepository.save(chatRoom_2);

    }

}
