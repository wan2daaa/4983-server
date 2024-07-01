package team.dankookie.server4983.scheduler.entity;

import jakarta.persistence.*;
import lombok.*;
import team.dankookie.server4983.chat.domain.ChatRoom;
import team.dankookie.server4983.scheduler.constant.ReportType;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AdminReport {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    private String message;

    private String buyerPhoneNumber;

    private String sellerPhoneNumber;

    private String reportType;

    public static AdminReport createAdminReport(ChatRoom chatRoom, ReportType reportType) {
        return AdminReport.builder()
                .chatRoom(chatRoom)
                .message(reportType.getMessage())
                .buyerPhoneNumber(chatRoom.getBuyer().getPhoneNumber())
                .sellerPhoneNumber(chatRoom.getSeller().getPhoneNumber())
                .reportType(reportType.getType())
                .build();
    }

}
