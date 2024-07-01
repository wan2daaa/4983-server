package team.dankookie.server4983.member.dto;

public record MemberMyPageResponse(
        String imageUrl,
        String nickname
) {
    public static MemberMyPageResponse of(String imageUrl, String nickname) {
        return new MemberMyPageResponse(imageUrl, nickname);
    }

}
