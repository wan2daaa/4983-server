package team.dankookie.server4983.book.repository.usedBook;

import lombok.extern.slf4j.Slf4j;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import team.dankookie.server4983.book.domain.UsedBook;
import team.dankookie.server4983.common.BaseRepositoryTest;
import team.dankookie.server4983.member.repository.MemberRepository;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
//@SpringBootTest
class UsedBookRepositoryBulkTest {

//    @Autowired
//    UsedBookRepository usedBookRepository;
//
//    @Autowired
//    MemberRepository memberRepository;

    @Test
    void easyRandomTest() {
        EasyRandomParameters parameters = new EasyRandomParameters()
                .excludeField(field -> field.getName().equals("id"));

//        UsedBook usedBook = new EasyRandom(parameters).nextObject(UsedBook.class);
//
//        UsedBook usedBook1 = new EasyRandom(parameters).nextObject(UsedBook.class);

        EasyRandom easyRandom = new EasyRandom(parameters);

        List<UsedBook> list = IntStream.range(0, 100)
                .parallel() // 병렬로 실행시켜서 객체 생서 속도 증가
                .mapToObj(i -> easyRandom.nextObject(UsedBook.class)).toList();

        for (UsedBook book : list) {
            log.info("book = {}", book);
            log.info("sellerMember = {}", book.getSellerMember());
        }
//        log.info("usedBook = {}", usedBook);
//        log.info("usedBook1 = {}", usedBook1);
    }
}