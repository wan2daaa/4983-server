package team.dankookie.server4983.scheduler.repository;

import team.dankookie.server4983.chat.domain.ChatRoom;
import team.dankookie.server4983.scheduler.constant.ReportType;
import team.dankookie.server4983.scheduler.entity.AdminReport;
import team.dankookie.server4983.scheduler.entity.Schedule;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CustomSchedulerRepository {

    List<Schedule> findByAlertTime(LocalDateTime time);

    List<ChatRoom> findChatRoomPreviouslyTime(long time, int interactStep);

    List<ChatRoom> findChatRoomAfterTime(long time, int interactStep);

    Optional<AdminReport> findByChatRoomAndReportType(ChatRoom chatRoom, ReportType reportType);

}
