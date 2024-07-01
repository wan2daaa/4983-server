package team.dankookie.server4983.member.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import team.dankookie.server4983.common.BaseControllerTest;
import team.dankookie.server4983.common.exception.ErrorResponse;
import team.dankookie.server4983.member.service.MemberService;

import static io.jsonwebtoken.lang.Strings.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MemberChangePasswordControllerTest extends BaseControllerTest {

    @Test
    void 현재비밀번호와_작성한_비밀번호가_같은경우_true를_리턴한다() throws Exception {
        //given
        final String password = "password";
        final String accessToken = "accessToken";

        when(memberService.isMemberPasswordMatch(any(), anyString()))
                .thenReturn(true);
        //when
        ResultActions resultActions = mockMvc.perform(post(API + "/my-pages/change-password/verify-current-password")
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"" + password + "\"}"))
                .andDo(print());

        //then
        resultActions.andExpect(status().isOk())
                .andDo(
                        document("my-pages/change-password/verify-current-password/success",
                                requestFields(
                                        fieldWithPath("password").description("검사할 현재 비밀번호")
                                ),
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION).description("accessToken")
                                ),
                                responseFields(
                                        fieldWithPath("isPasswordMatch").description("패스워드가 일치하는지 여부")
                                )
                        )
                );


    }

    @Test
    void 회원이_존재하지_않는경우_에러를_리턴한다() throws Exception {
        //given
        final String password = "password";
        final String accessToken = "accessToken";

        when(memberService.isMemberPasswordMatch(any(), anyString()))
                .thenThrow(new IllegalArgumentException("존재하지 않는 사용자입니다."));
        //when
        ResultActions resultActions = mockMvc.perform(post(API + "/my-pages/change-password/verify-current-password")
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"" + password + "\"}"))
                .andDo(print());

        //then
        resultActions.andExpect(status().isBadRequest())
                .andDo(
                        document("my-pages/change-password/verify-current-password/fail",
                                requestFields(
                                        fieldWithPath("password").description("검사할 현재 비밀번호")
                                ),
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION).description("accessToken")
                                ),
                                responseFields(
                                        fieldWithPath("message").description("에러 메세지")
                                )
                        )
                );

        String contentAsString = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
        assertThat(contentAsString).isEqualTo(objectMapper.writeValueAsString(ErrorResponse.of("존재하지 않는 사용자입니다.")));


    }

}