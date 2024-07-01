package team.dankookie.server4983.member.controller;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.dankookie.server4983.jwt.dto.AccessToken;
import team.dankookie.server4983.member.service.MemberService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class MemberWithdrawController {
    private final MemberService memberService;

    @PatchMapping("/withdraw")
    public ResponseEntity<String> withdraw(HttpServletResponse response, AccessToken accessToken, @CookieValue(name = "refreshToken", required = false) Cookie refreshTokenCookie) {

        boolean isWithdraw = memberService.checkMemberAndWithdraw(accessToken);
        if (isWithdraw) {
            refreshTokenCookie.setMaxAge(0);
            response.addCookie(refreshTokenCookie);
            return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");

        } else {
            return ResponseEntity.badRequest().body("이미 탈퇴한 회원이거나 회원을 찾을 수 없습니다.");
        }
    }
}

