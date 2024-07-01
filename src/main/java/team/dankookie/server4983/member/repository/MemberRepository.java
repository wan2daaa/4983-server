package team.dankookie.server4983.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.dankookie.server4983.member.domain.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    Optional<Member> findByNickname(String nickname);

    Optional<Member> findByStudentId(String studentId);

    boolean existsMemberByNickname(String nickname);

    boolean existsByStudentId(String studentId);

    boolean existsByNickname(String nickname);

    boolean existsMemberByPhoneNumber(String phoneNumber);
}
