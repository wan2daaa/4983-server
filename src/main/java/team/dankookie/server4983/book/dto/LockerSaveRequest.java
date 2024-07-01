package team.dankookie.server4983.book.dto;

import static team.dankookie.server4983.chat.constant.ContentType.BOOK_PLACEMENT_SET;

import java.time.LocalDate;

import team.dankookie.server4983.book.domain.Locker;
import team.dankookie.server4983.chat.domain.ChatRoom;
import team.dankookie.server4983.chat.dto.ChatRequest;

public record LockerSaveRequest(
        Integer lockerNumber,
        String password,
        Long chatRoomId
) {

    public static LockerSaveRequest of(Integer lockerNumber, String password, Long chatRoomId) {
        return new LockerSaveRequest(lockerNumber, password, chatRoomId);
    }

    public Locker toEntity(ChatRoom chatRoom, LocalDate tradeDate) {
        return Locker.builder()
                .lockerNumber(lockerNumber)
                .chatRoom(chatRoom)
                .password(password)
                .isExists(true)
                .tradeDate(tradeDate)
                .build();
    }

    public ChatRequest toChatRequest() {
        return ChatRequest.of(chatRoomId, BOOK_PLACEMENT_SET);
    }

}
