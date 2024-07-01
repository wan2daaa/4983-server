package team.dankookie.server4983.member.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import team.dankookie.server4983.book.constant.College;
import team.dankookie.server4983.book.constant.Department;
import team.dankookie.server4983.common.domain.BaseEntity;
import team.dankookie.server4983.member.constant.AccountBank;
import team.dankookie.server4983.member.constant.UserRole;
import team.dankookie.server4983.member.dto.MemberProfileSaveRequest;

import java.util.Collection;
import java.util.List;

@ToString
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Member extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String studentId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private College college;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Department department;

    @NotNull
    private Integer yearOfAdmission;

    @NotNull
    @Column(unique = true)
    private String nickname;

    @NotNull
    private String password;

    @NotNull
    private String phoneNumber;

    @NotNull
    private String accountHolder;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AccountBank accountBank;

    @NotNull
    private String accountNumber;

    @Setter
    private String imageUrl;

    @ColumnDefault("false")
    private Boolean marketingAgree;

    private String firebaseToken;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @ColumnDefault("false")
    private Boolean isWithdraw;

    @ColumnDefault("false")
    private Boolean isBlocked;

    public void changePassword(String password) {
        this.password = password;
    }

    @Builder
    public Member(Long id, String studentId, College college, Department department,
                  Integer yearOfAdmission, String nickname, String password, String phoneNumber,
                  String accountHolder, AccountBank accountBank, String accountNumber, String imageUrl,
                  Boolean marketingAgree, String firebaseToken, UserRole role, Boolean isWithdraw,
                  Boolean isBlocked) {
        this.id = id;
        this.studentId = studentId;
        this.college = college;
        this.department = department;
        this.yearOfAdmission = yearOfAdmission;
        this.nickname = nickname;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.accountHolder = accountHolder;
        this.accountBank = accountBank;
        this.accountNumber = accountNumber;
        this.imageUrl = imageUrl;
        this.marketingAgree = marketingAgree;
        this.firebaseToken = firebaseToken;
        this.role = role;
        this.isWithdraw = isWithdraw;
        this.isBlocked = isBlocked;
    }

    public void withdraw() {
        if (!this.isWithdraw) {
            this.isWithdraw = true;
        }
    }

    public boolean updateBlocked() {
        this.isBlocked = !this.isBlocked;
        return this.isBlocked;
    }

    public void updateMemberProfile(MemberProfileSaveRequest member) {
        this.nickname = member.nickname();
        this.accountBank = member.accountBank();
        this.accountNumber = member.accountNumber();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return nickname;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !getDelYn();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !getDelYn();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !getDelYn();
    }

    @Override
    public boolean isEnabled() {
        return !getDelYn();
    }

}
