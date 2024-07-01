package team.dankookie.server4983.member.controller;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static team.dankookie.server4983.jwt.constants.TokenDuration.ACCESS_TOKEN_DURATION;
import static team.dankookie.server4983.jwt.constants.TokenDuration.REFRESH_TOKEN_DURATION;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.dankookie.server4983.jwt.domain.RefreshToken;
import team.dankookie.server4983.jwt.service.RefreshTokenService;
import team.dankookie.server4983.jwt.util.JwtTokenUtils;
import team.dankookie.server4983.member.domain.Member;
import team.dankookie.server4983.member.dto.AdminLoginRequest;
import team.dankookie.server4983.member.service.AdminMemberService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/login")
public class AdminLoginController {

    private final AdminMemberService adminMemberService;
    private final JwtTokenUtils jwtTokenUtils;
    private final RefreshTokenService refreshTokenService;

    @PostMapping
    public ResponseEntity<Void> login(@RequestBody AdminLoginRequest request, HttpServletResponse response) {
        adminMemberService.login(request);

        Member member = adminMemberService.findMemberNicknameById(request.id());

        setAccessTokenToHeader(response, member);
        setRefreshTokenToCookie(response, member);

        return ResponseEntity.ok().build();
    }

    private void setAccessTokenToHeader(HttpServletResponse response, Member member) {
        String accessToken = jwtTokenUtils.generateJwtToken(member.getNickname(), ACCESS_TOKEN_DURATION.getDuration());
        response.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
    }

    private void setRefreshTokenToCookie(HttpServletResponse response, Member member) {
        String refreshToken = jwtTokenUtils.generateJwtToken(member.getNickname(), REFRESH_TOKEN_DURATION.getDuration());
        refreshTokenService.save(
                member,
                RefreshToken.builder()
                        .member(member)
                        .refreshToken(refreshToken)
                        .build()
        );
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge((int) REFRESH_TOKEN_DURATION.getDuration() / 1000);
        response.addCookie(refreshTokenCookie);
    }

}
