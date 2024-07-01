package team.dankookie.server4983.jwt.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import team.dankookie.server4983.common.exception.NotAuthorizedException;
import team.dankookie.server4983.jwt.constants.TokenSecretKey;
import team.dankookie.server4983.jwt.dto.AccessToken;
import team.dankookie.server4983.jwt.util.JwtTokenUtils;

@Slf4j
@RequiredArgsConstructor
public class LoginResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenUtils jwtTokenUtils;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(AccessToken.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        String accessToken = webRequest.getHeader(HttpHeaders.AUTHORIZATION);

        if (accessToken == null || accessToken.equals("")) {
            log.error("accessToken 토큰이 존재하지 않습니다.");
            throw new NotAuthorizedException();
        }

        if (jwtTokenUtils.isTokenExpired(accessToken)) {

            log.error("accessToken 토큰이 만료되었습니다.");
            throw new NotAuthorizedException();
        }

        String nickname = jwtTokenUtils.getNickname(accessToken);

        return AccessToken.of(accessToken, nickname);
    }
}
