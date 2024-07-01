package team.dankookie.server4983.member.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import team.dankookie.server4983.book.constant.Department;
import team.dankookie.server4983.member.constant.AccountBank;

@RequiredArgsConstructor
@Getter
public class AdminMemberListResponse {
    private final Long id;
    private final String studentId;
    private final String department;
    private final String nickname;
    private final String phoneNumber;
    private final String accountBank;
    private final String accountNumber;
    private final String accountHolder;
    private final Boolean isBlocked;


    @QueryProjection
    public AdminMemberListResponse(Long id, String studentId, Department department, String nickname, String phoneNumber, AccountBank accountBank, String accountNumber, String accountHolder, boolean isBlocked) {
        this.id = id;
        this.studentId = studentId;
        this.department = department.getKoName();
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.accountBank = accountBank.getKoBankName();
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.isBlocked = isBlocked;
    }
}
