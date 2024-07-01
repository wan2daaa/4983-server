package team.dankookie.server4983.member.dto;

public record MemberProfileSaveResponse(
        Long memberId
) {
    public static MemberProfileSaveResponse of(Long memberId) {
        return new MemberProfileSaveResponse(memberId);
    }
}
