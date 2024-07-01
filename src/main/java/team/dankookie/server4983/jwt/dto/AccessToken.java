package team.dankookie.server4983.jwt.dto;

public record AccessToken(
        String value,
        String nickname
) {
    public static AccessToken of(String value, String nickname) {
        return new AccessToken(value, nickname);
    }
}
