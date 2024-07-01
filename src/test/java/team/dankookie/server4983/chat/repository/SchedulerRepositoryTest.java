package team.dankookie.server4983.chat.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import team.dankookie.server4983.book.domain.UsedBook;
import team.dankookie.server4983.book.repository.usedBook.UsedBookRepository;
import team.dankookie.server4983.chat.domain.ChatRoom;
import team.dankookie.server4983.common.BaseRepositoryTest;
import team.dankookie.server4983.member.domain.Member;
import team.dankookie.server4983.member.fixture.MemberFixture;
import team.dankookie.server4983.member.repository.MemberRepository;
import team.dankookie.server4983.scheduler.entity.Schedule;
import team.dankookie.server4983.scheduler.repository.SchedulerRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SchedulerRepositoryTest extends BaseRepositoryTest {

    @Autowired
    SchedulerRepository schedulerRepository;

    @Autowired
    UsedBookRepository usedBookRepository;

    @Autowired
    ChatRoomRepository chatRoomRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void 특정시간에작동되는스케쥴러가져오기() {
        // given
        schedulerRepository.save(Schedule.builder().message("message1").time(LocalDateTime.of(2023, 3, 2, 10, 0, 0)).build());
        schedulerRepository.save(Schedule.builder().message("message2").time(LocalDateTime.of(2023, 3, 2, 10, 10, 0)).build());
        schedulerRepository.save(Schedule.builder().message("message3").time(LocalDateTime.of(2023, 3, 2, 10, 20, 0)).build());
        schedulerRepository.save(Schedule.builder().message("message4").time(LocalDateTime.of(2023, 3, 2, 10, 20, 0)).build());
        schedulerRepository.save(Schedule.builder().message("message5").time(LocalDateTime.of(2023, 3, 2, 10, 30, 0)).build());
        schedulerRepository.save(Schedule.builder().message("message6").time(LocalDateTime.of(2023, 3, 2, 10, 40, 0)).build());
        schedulerRepository.save(Schedule.builder().message("message7").time(LocalDateTime.of(2023, 3, 3, 10, 0, 0)).build());
        schedulerRepository.save(Schedule.builder().message("message8").time(LocalDateTime.of(2023, 3, 3, 10, 10, 0)).build());
        schedulerRepository.save(Schedule.builder().message("message9").time(LocalDateTime.of(2023, 3, 2, 10, 20, 0)).build());
        schedulerRepository.save(Schedule.builder().message("message10").time(LocalDateTime.of(2023, 3, 3, 10, 30, 0)).build());

        // when
        List<Schedule> result = schedulerRepository.findByAlertTime(LocalDateTime.of(2023, 3, 2, 10, 20, 0));

        // then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0).getMessage()).isEqualTo("message3");
        assertThat(result.get(1).getMessage()).isEqualTo("message4");
        assertThat(result.get(2).getMessage()).isEqualTo("message9");
    }

    @Test
    public void 특정시간이전의채팅방가져오기() {
        // given
        Member member = memberRepository.save(MemberFixture.createMemberByNickname("nickname"));
        UsedBook usedBook01 = usedBookRepository.save(UsedBook.builder().name("name01").price(1).tradeAvailableDatetime(LocalDateTime.now().minusMinutes(20)).sellerMember(member).build());
        chatRoomRepository.save(ChatRoom.builder().usedBook(usedBook01).interactStep(0).build());
        UsedBook usedBook02 = usedBookRepository.save(UsedBook.builder().name("name01").price(1).tradeAvailableDatetime(LocalDateTime.now().minusMinutes(10)).sellerMember(member).build());
        chatRoomRepository.save(ChatRoom.builder().usedBook(usedBook02).interactStep(0).build());
        UsedBook usedBook03 = usedBookRepository.save(UsedBook.builder().name("name01").price(1).tradeAvailableDatetime(LocalDateTime.now().minusMinutes(25)).sellerMember(member).build());
        chatRoomRepository.save(ChatRoom.builder().usedBook(usedBook03).interactStep(0).build());

        UsedBook usedBook04 = usedBookRepository.save(UsedBook.builder().name("name01").price(1).tradeAvailableDatetime(LocalDateTime.now().minusMinutes(20)).sellerMember(member).build());
        chatRoomRepository.save(ChatRoom.builder().usedBook(usedBook04).interactStep(2).build());
        UsedBook usedBook05 = usedBookRepository.save(UsedBook.builder().name("name01").price(1).tradeAvailableDatetime(LocalDateTime.now().minusMinutes(10)).sellerMember(member).build());
        chatRoomRepository.save(ChatRoom.builder().usedBook(usedBook05).interactStep(3).build());
        UsedBook usedBook06 = usedBookRepository.save(UsedBook.builder().name("name01").price(1).tradeAvailableDatetime(LocalDateTime.now().minusMinutes(40)).sellerMember(member).build());
        chatRoomRepository.save(ChatRoom.builder().usedBook(usedBook06).interactStep(0).build());

        // when
        List<ChatRoom> result = schedulerRepository.findChatRoomPreviouslyTime(30, 0);

        // then
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    public void 특정날짜이전의채팅방가져오기() {
        // given
        Member member = memberRepository.save(MemberFixture.createMemberByNickname("nickname"));
        UsedBook usedBook01 = usedBookRepository.save(UsedBook.builder().name("name01").price(1).tradeAvailableDatetime(LocalDateTime.now().minusMinutes(144)).sellerMember(member).build());
        chatRoomRepository.save(ChatRoom.builder().usedBook(usedBook01).interactStep(0).build());
        UsedBook usedBook02 = usedBookRepository.save(UsedBook.builder().name("name01").price(1).tradeAvailableDatetime(LocalDateTime.now().minusMinutes(288)).sellerMember(member).build());
        chatRoomRepository.save(ChatRoom.builder().usedBook(usedBook02).interactStep(0).build());
        UsedBook usedBook03 = usedBookRepository.save(UsedBook.builder().name("name01").price(1).tradeAvailableDatetime(LocalDateTime.now().minusMinutes(120)).sellerMember(member).build());
        chatRoomRepository.save(ChatRoom.builder().usedBook(usedBook03).interactStep(0).build());

        UsedBook usedBook04 = usedBookRepository.save(UsedBook.builder().name("name01").price(1).tradeAvailableDatetime(LocalDateTime.now().minusMinutes(144)).sellerMember(member).build());
        chatRoomRepository.save(ChatRoom.builder().usedBook(usedBook04).interactStep(2).build());
        UsedBook usedBook05 = usedBookRepository.save(UsedBook.builder().name("name01").price(1).tradeAvailableDatetime(LocalDateTime.now().minusMinutes(288)).sellerMember(member).build());
        chatRoomRepository.save(ChatRoom.builder().usedBook(usedBook05).interactStep(3).build());
        UsedBook usedBook06 = usedBookRepository.save(UsedBook.builder().name("name01").price(1).tradeAvailableDatetime(LocalDateTime.now().minusMinutes(450)).sellerMember(member).build());
        chatRoomRepository.save(ChatRoom.builder().usedBook(usedBook06).interactStep(0).build());

        // when
        List<ChatRoom> result = schedulerRepository.findChatRoomPreviouslyTime(300, 0);

        // then
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    public void 특정시간이후의채팅방가져오기() {
        // given
        Member member = memberRepository.save(MemberFixture.createMemberByNickname("nickname"));
        UsedBook usedBook01 = usedBookRepository.save(UsedBook.builder().name("name01").price(1).tradeAvailableDatetime(LocalDateTime.now().plusMinutes(20)).sellerMember(member).build());
        chatRoomRepository.save(ChatRoom.builder().usedBook(usedBook01).interactStep(0).build());
        UsedBook usedBook02 = usedBookRepository.save(UsedBook.builder().name("name01").price(1).tradeAvailableDatetime(LocalDateTime.now().plusMinutes(10)).sellerMember(member).build());
        chatRoomRepository.save(ChatRoom.builder().usedBook(usedBook02).interactStep(0).build());
        UsedBook usedBook03 = usedBookRepository.save(UsedBook.builder().name("name01").price(1).tradeAvailableDatetime(LocalDateTime.now().plusMinutes(25)).sellerMember(member).build());
        chatRoomRepository.save(ChatRoom.builder().usedBook(usedBook03).interactStep(0).build());

        UsedBook usedBook04 = usedBookRepository.save(UsedBook.builder().name("name01").price(1).tradeAvailableDatetime(LocalDateTime.now().plusMinutes(20)).sellerMember(member).build());
        chatRoomRepository.save(ChatRoom.builder().usedBook(usedBook04).interactStep(2).build());
        UsedBook usedBook05 = usedBookRepository.save(UsedBook.builder().name("name01").price(1).tradeAvailableDatetime(LocalDateTime.now().plusMinutes(10)).sellerMember(member).build());
        chatRoomRepository.save(ChatRoom.builder().usedBook(usedBook05).interactStep(3).build());
        UsedBook usedBook06 = usedBookRepository.save(UsedBook.builder().name("name01").price(1).tradeAvailableDatetime(LocalDateTime.now().plusMinutes(40)).sellerMember(member).build());
        chatRoomRepository.save(ChatRoom.builder().usedBook(usedBook06).interactStep(0).build());

        // when
        List<ChatRoom> result = schedulerRepository.findChatRoomAfterTime(30, 0);

        // then
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    public void 특정날짜이후의채팅방가져오기() {
        // given
        Member member = memberRepository.save(MemberFixture.createMemberByNickname("nickname"));
        UsedBook usedBook01 = usedBookRepository.save(UsedBook.builder().name("name01").price(1).tradeAvailableDatetime(LocalDateTime.now().plusMinutes(200)).sellerMember(member).build());
        chatRoomRepository.save(ChatRoom.builder().usedBook(usedBook01).interactStep(0).build());
        UsedBook usedBook02 = usedBookRepository.save(UsedBook.builder().name("name01").price(1).tradeAvailableDatetime(LocalDateTime.now().plusMinutes(100)).sellerMember(member).build());
        chatRoomRepository.save(ChatRoom.builder().usedBook(usedBook02).interactStep(0).build());
        UsedBook usedBook03 = usedBookRepository.save(UsedBook.builder().name("name01").price(1).tradeAvailableDatetime(LocalDateTime.now().plusMinutes(250)).sellerMember(member).build());
        chatRoomRepository.save(ChatRoom.builder().usedBook(usedBook03).interactStep(0).build());

        UsedBook usedBook04 = usedBookRepository.save(UsedBook.builder().name("name01").price(1).tradeAvailableDatetime(LocalDateTime.now().plusMinutes(200)).sellerMember(member).build());
        chatRoomRepository.save(ChatRoom.builder().usedBook(usedBook04).interactStep(2).build());
        UsedBook usedBook05 = usedBookRepository.save(UsedBook.builder().name("name01").price(1).tradeAvailableDatetime(LocalDateTime.now().plusMinutes(100)).sellerMember(member).build());
        chatRoomRepository.save(ChatRoom.builder().usedBook(usedBook05).interactStep(3).build());
        UsedBook usedBook06 = usedBookRepository.save(UsedBook.builder().name("name01").price(1).tradeAvailableDatetime(LocalDateTime.now().plusMinutes(400)).sellerMember(member).build());
        chatRoomRepository.save(ChatRoom.builder().usedBook(usedBook06).interactStep(0).build());

        // when
        List<ChatRoom> result = schedulerRepository.findChatRoomAfterTime(300, 0);

        // then
        assertThat(result.size()).isEqualTo(3);
    }

}
