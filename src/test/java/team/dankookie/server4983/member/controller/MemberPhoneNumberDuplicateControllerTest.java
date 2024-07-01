package team.dankookie.server4983.member.controller;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;
import team.dankookie.server4983.common.BaseControllerTest;
import team.dankookie.server4983.jwt.constants.TokenDuration;
import team.dankookie.server4983.member.service.MemberService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberPhoneNumberDuplicateControllerTest extends BaseControllerTest {

    @Test
    void 멤버의_전화번호가_중복인경우_true를_리턴한다() throws Exception {
        //given
        String accessToken = jwtTokenUtils.generateJwtToken("nickname", TokenDuration.ACCESS_TOKEN_DURATION.getDuration());
        String phoneNumber = "010-1234-1234";
        when(memberService.checkPhoneNumberDuplicate(any(), anyString())).thenReturn(true);
        //when
        ResultActions resultActions = mockMvc.perform(get(API + "/my-pages/phoneNumber/duplicate")
                        .param("phoneNumber", phoneNumber)
                        .header(HttpHeaders.AUTHORIZATION, accessToken))
                .andDo(print());


        //then
        resultActions.andExpect(status().isOk())
                .andDo(document("my-pages/phoneNumber/duplicate/success",
                        queryParameters(
                                parameterWithName("phoneNumber").description("중복 검사할 핸드폰 번호")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("accessToken")
                        ),
                        responseFields(
                                fieldWithPath("phoneNumberDuplicate").description("핸드폰 번호 중복 여부")
                        )
                ));
    }

}