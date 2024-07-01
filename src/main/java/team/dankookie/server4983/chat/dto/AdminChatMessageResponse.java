package team.dankookie.server4983.chat.dto;

import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AdminChatMessageResponse {

    private final String message;
    private final LocalTime time;

    @QueryProjection
    public AdminChatMessageResponse(String message, LocalDateTime dateTime) {
        this.message = message;
        this.time = dateTime.toLocalTime();

    }

}
