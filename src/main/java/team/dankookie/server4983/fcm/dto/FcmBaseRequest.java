package team.dankookie.server4983.fcm.dto;

public record FcmBaseRequest(
        Long targetUserId,
        String title,
        String body
) {

    public static FcmBaseRequest of(Long targetUserId, String title, String body) {
        return new FcmBaseRequest(targetUserId, title, body);
    }
}
