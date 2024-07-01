package team.dankookie.server4983.scheduler.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import team.dankookie.server4983.chat.constant.ContentType;
import team.dankookie.server4983.chat.domain.ChatRoom;
import team.dankookie.server4983.scheduler.constant.ReportType;
import team.dankookie.server4983.scheduler.entity.AdminReport;
import team.dankookie.server4983.scheduler.entity.QAdminReport;
import team.dankookie.server4983.scheduler.entity.Schedule;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static team.dankookie.server4983.book.domain.QUsedBook.usedBook;
import static team.dankookie.server4983.chat.domain.QChatRoom.chatRoom;
import static team.dankookie.server4983.scheduler.entity.QSchedule.schedule;

@RequiredArgsConstructor
public class SchedulerRepositoryImpl implements CustomSchedulerRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Schedule> findByAlertTime(LocalDateTime time) {
        LocalDateTime start = time.withNano(0).minusMinutes(3);
        LocalDateTime end = time.withNano(0).plusMinutes(3);

        return jpaQueryFactory.select(schedule)
                .from(schedule)
                .where(schedule.time.goe(start).and(schedule.time.loe(end)))
                .fetch();
    }

    @Override
    public List<ChatRoom> findChatRoomPreviouslyTime(long time, int interactStep) {
        LocalDateTime start = LocalDateTime.now().minusMinutes(time);
        LocalDateTime end = LocalDateTime.now();

        return jpaQueryFactory.select(chatRoom).from(chatRoom)
                .innerJoin(chatRoom.usedBook, usedBook)
                .on(usedBook.tradeAvailableDatetime.goe(start).and(usedBook.tradeAvailableDatetime.loe(end)))
                .where(chatRoom.interactStep.eq(interactStep))
                .fetch();
    }

    @Override
    public List<ChatRoom> findChatRoomAfterTime(long time, int interactStep) {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusMinutes(time);

        return jpaQueryFactory.select(chatRoom).from(chatRoom)
                .innerJoin(chatRoom.usedBook, usedBook)
                .on(usedBook.tradeAvailableDatetime.goe(start).and(usedBook.tradeAvailableDatetime.loe(end)))
                .where(chatRoom.interactStep.eq(interactStep))
                .fetch();
    }

    @Override
    public Optional<AdminReport> findByChatRoomAndReportType(ChatRoom chatRoom, ReportType reportType) {
        QAdminReport adminReport = new QAdminReport("admin");
        AdminReport result = jpaQueryFactory.select(adminReport).from(adminReport)
                .where(adminReport.chatRoom.eq(chatRoom).and(adminReport.reportType.eq(reportType.getType())))
                .fetchOne();

        return Optional.ofNullable(result);
    }

}
