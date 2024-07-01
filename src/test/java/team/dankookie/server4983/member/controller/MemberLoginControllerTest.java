package team.dankookie.server4983.member.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.cookies.CookieDocumentation;
import org.springframework.restdocs.headers.HeaderDocumentation;
import org.springframework.test.web.servlet.ResultActions;
import team.dankookie.server4983.common.BaseControllerTest;
import team.dankookie.server4983.common.exception.ErrorResponse;
import team.dankookie.server4983.common.exception.LoginFailedException;
import team.dankookie.server4983.jwt.service.RefreshTokenService;
import team.dankookie.server4983.member.domain.Member;
import team.dankookie.server4983.member.dto.LoginRequest;
import team.dankookie.server4983.member.service.MemberService;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberLoginControllerTest extends BaseControllerTest {

    @Test
    void 로그인시_accessToken과_refreshToken을_리턴한다() throws Exception {
        //given
        String loginUrl = API + "/login";

        LoginRequest loginRequest = LoginRequest.of("studentId", "password");


        when(memberService.login(loginRequest))
                .thenReturn(true);
        when(memberService.findMemberNicknameByStudentId(any()))
                .thenReturn(Member.builder().nickname("nickname").studentId("studentId").password("password").build());

        //when
        ResultActions resultActions = mockMvc.perform(post(loginUrl)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(loginRequest))
        ).andDo(print());

        //then
        resultActions.andExpect(status().isOk())
                .andDo(
                        document("member/login/success",
                                requestFields(
                                        fieldWithPath("studentId").description("이메일"),
                                        fieldWithPath("password").description("비밀번호")
                                ),
                                CookieDocumentation.responseCookies(
                                        CookieDocumentation.cookieWithName("refreshToken").description("refreshToken")
                                ),
                                HeaderDocumentation.responseHeaders(
                                        headerWithName("Authorization").description("accessToken")

                                )
                        )
                );
        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
        assertThat(content).isEmpty();
    }

    @Test
    void 로그인시_학번이_없으면_에러를_던진다() throws Exception {
        //given
        String loginUrl = API + "/login";

        LoginRequest loginRequest = LoginRequest.of("studentId", "password");


        when(memberService.login(loginRequest))
                .thenThrow(new LoginFailedException("존재하지 않는 학번입니다."));

        //when
        ResultActions resultActions = mockMvc.perform(post(loginUrl)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(loginRequest))
        ).andDo(print());

        //then
        resultActions.andExpect(status().isUnauthorized())
                .andDo(
                        document("member/login/fail/studentId",
                                requestFields(
                                        fieldWithPath("studentId").description("이메일"),
                                        fieldWithPath("password").description("비밀번호")
                                ),
                                responseFields(
                                        fieldWithPath("message").description("에러 메시지")
                                )
                        )
                );

        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
        ErrorResponse response = ErrorResponse.of("존재하지 않는 학번입니다.");
        assertThat(content).isEqualTo(objectMapper.writeValueAsString(response));
    }

    @Test
    void 로그인시_비밀번호가_일치하지_않으면_에러를_던진다() throws Exception {
        //given
        String loginUrl = API + "/login";

        LoginRequest loginRequest = LoginRequest.of("studentId", "password");


        when(memberService.login(loginRequest))
                .thenThrow(new LoginFailedException("잘못된 비밀번호입니다!"));

        //when
        ResultActions resultActions = mockMvc.perform(post(loginUrl)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(loginRequest))
        ).andDo(print());

        //then
        resultActions.andExpect(status().isUnauthorized())
                .andDo(
                        document("member/login/fail/password",
                                requestFields(
                                        fieldWithPath("studentId").description("이메일"),
                                        fieldWithPath("password").description("비밀번호")
                                ),
                                responseFields(
                                        fieldWithPath("message").description("에러 메시지")
                                )
                        )
                );

        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
        ErrorResponse response = ErrorResponse.of("잘못된 비밀번호입니다!");
        assertThat(content).isEqualTo(objectMapper.writeValueAsString(response));
    }

}