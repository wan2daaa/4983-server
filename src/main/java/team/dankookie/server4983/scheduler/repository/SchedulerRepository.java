package team.dankookie.server4983.scheduler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.dankookie.server4983.chat.domain.ChatRoom;
import team.dankookie.server4983.scheduler.constant.ScheduleType;
import team.dankookie.server4983.scheduler.entity.Schedule;

public interface SchedulerRepository extends JpaRepository<Schedule, Long>, CustomSchedulerRepository {

    long deleteByChatRoomAndScheduleType(ChatRoom chatRoom, ScheduleType scheduleType);

}
