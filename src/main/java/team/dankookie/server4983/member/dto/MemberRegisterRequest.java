package team.dankookie.server4983.member.dto;

import team.dankookie.server4983.book.constant.Department;
import team.dankookie.server4983.member.constant.AccountBank;
import team.dankookie.server4983.member.constant.UserRole;
import team.dankookie.server4983.member.domain.Member;

public record MemberRegisterRequest(
        String studentId,
        Department department,
        int yearOfAdmission,
        String nickname,
        String password,
        String phoneNumber,
        boolean marketingAgree,
        String accountHolder,
        AccountBank accountBank,
        String accountNumber,
        String firebaseToken

) {

    public static MemberRegisterRequest of(String studentId,
                                           Department department,
                                           int yearOfAdmission,
                                           String nickname,
                                           String password,
                                           String phoneNumber,
                                           boolean marketingAgree,
                                           String accountHolder,
                                           AccountBank accountBank,
                                           String accountNumber,
                                           String firebaseToken
    ) {
        return new MemberRegisterRequest(
                studentId,
                department,
                yearOfAdmission,
                nickname,
                password,
                phoneNumber,
                marketingAgree,
                accountHolder,
                accountBank,
                accountNumber,
                firebaseToken
        );
    }

    public Member toEntity() {
        return Member.builder()
                .studentId(studentId)
                .department(department)
                .yearOfAdmission(yearOfAdmission)
                .nickname(nickname)
                .password(password)
                .phoneNumber(phoneNumber)
                .marketingAgree(marketingAgree)
                .accountHolder(accountHolder)
                .accountBank(accountBank)
                .accountNumber(accountNumber)
                .build();
    }

    public Member toEntity(String password) {
        return Member.builder()
                .studentId(studentId)
                .college(department.getCollege())
                .department(department)
                .yearOfAdmission(yearOfAdmission)
                .nickname(nickname)
                .password(password)
                .phoneNumber(phoneNumber)
                .marketingAgree(marketingAgree)
                .accountHolder(accountHolder)
                .accountBank(accountBank)
                .accountNumber(accountNumber)
                .firebaseToken(firebaseToken)
                .role(UserRole.USER)
                .imageUrl("https://4983-s3.s3.ap-northeast-2.amazonaws.com/baseImage.png")
                .build();
    }
}
