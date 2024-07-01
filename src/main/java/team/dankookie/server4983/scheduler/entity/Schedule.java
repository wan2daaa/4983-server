package team.dankookie.server4983.scheduler.entity;

import jakarta.persistence.*;
import lombok.*;
import team.dankookie.server4983.chat.domain.ChatRoom;
import team.dankookie.server4983.scheduler.constant.ScheduleType;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    private long targetId;

    private LocalDateTime time;

    private String message;

    private ScheduleType scheduleType;
}
