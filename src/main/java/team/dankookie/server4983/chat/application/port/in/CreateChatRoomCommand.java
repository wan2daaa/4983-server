package team.dankookie.server4983.chat.application.port.in;

public record CreateChatRoomCommand(
        Long usedBookId,
        String nickname
) {
    public static CreateChatRoomCommand of(Long usedBookId, String nickname) {
        return new CreateChatRoomCommand(usedBookId, nickname);
    }
}
