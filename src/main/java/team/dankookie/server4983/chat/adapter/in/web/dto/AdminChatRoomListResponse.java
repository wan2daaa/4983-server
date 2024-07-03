package team.dankookie.server4983.chat.adapter.in.web.dto;

import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AdminChatRoomListResponse {
    private final Long chatRoomId;
    private final String sellerStudentId;
    private final Integer interact;
    private final String usedBookName;
    private final Integer lockerNumber;
    private final String lockerPassword;
    private final String buyerStudentId;
    private final LocalDate tradeAvailableDate;
    private final LocalTime tradeAvailableTime;


    @QueryProjection
    public AdminChatRoomListResponse(Long chatRoomId, String sellerStudentId, Integer interact, String usedBookName,
                                     Integer lockerNumber, String lockerPassword, String buyerStudentId,
                                     LocalDateTime tradeAvailableDateTime) {
        this.chatRoomId = chatRoomId;
        this.sellerStudentId = sellerStudentId;
        this.interact = interact;
        this.usedBookName = usedBookName;
        this.lockerNumber = lockerNumber;
        this.lockerPassword = lockerPassword;
        this.buyerStudentId = buyerStudentId;
        this.tradeAvailableDate = tradeAvailableDateTime.toLocalDate();
        this.tradeAvailableTime = tradeAvailableDateTime.toLocalTime();
    }
}
