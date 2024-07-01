package team.dankookie.server4983.member.dto;


public record MemberPasswordMatchResponse(
        boolean isPasswordMatch
) {

    public static MemberPasswordMatchResponse of(boolean currentPassword) {
        return new MemberPasswordMatchResponse(currentPassword);
    }
}
