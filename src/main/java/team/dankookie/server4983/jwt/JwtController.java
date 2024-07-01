package team.dankookie.server4983.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.dankookie.server4983.jwt.constants.TokenDuration;
import team.dankookie.server4983.jwt.util.JwtTokenUtils;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/token")
public class JwtController {

    private final JwtTokenUtils jwtTokenUtils;

    @GetMapping("/valid")
    public ResponseEntity<Void> isAccessTokenValid(HttpServletRequest request,
                                                   HttpServletResponse response) {

        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (accessToken == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).build();
        }

        String nickname = jwtTokenUtils.getNickname(accessToken);
        Boolean validate = jwtTokenUtils.validate(accessToken, nickname);

        if (validate) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).build();
        }
    }

    @GetMapping("/update")
    public ResponseEntity<Void> updateAccessToken(
            @CookieValue(name = "refreshToken") Cookie refreshTokenCookie
    ) {

        String refreshToken = refreshTokenCookie.getValue();

        String nickname = jwtTokenUtils.getNickname(refreshToken);
        Boolean validate = jwtTokenUtils.validate(refreshToken, nickname);

        if (validate) {
            String newAccessToken = jwtTokenUtils.generateJwtToken(nickname,
                    TokenDuration.ACCESS_TOKEN_DURATION.getDuration());
            return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, newAccessToken).build();
        } else {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).build();
        }
    }


}
