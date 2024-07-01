package team.dankookie.server4983.member.dto;

public record MemberPasswordChangeRequest(
        String studentId,

        String phoneNumber,

        String password
) {

    public static MemberPasswordChangeRequest of(String studentId, String phoneNumber, String password) {
        return new MemberPasswordChangeRequest(studentId, phoneNumber, password);
    }
}
