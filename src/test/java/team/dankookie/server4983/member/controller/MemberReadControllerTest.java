package team.dankookie.server4983.member.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;
import team.dankookie.server4983.book.constant.Department;
import team.dankookie.server4983.common.BaseControllerTest;
import team.dankookie.server4983.jwt.constants.TokenDuration;
import team.dankookie.server4983.jwt.dto.AccessToken;
import team.dankookie.server4983.member.dto.MemberCollegeAndDepartment;
import team.dankookie.server4983.member.service.MemberService;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberReadControllerTest extends BaseControllerTest {

    @Test
    void 멤버의_단과대와_학과를_리턴한다() throws Exception {
        //given
        String accessToken = jwtTokenUtils.generateJwtToken("nickname", TokenDuration.ACCESS_TOKEN_DURATION.getDuration());

        when(memberService.findMemberCollegeAndDepartment(AccessToken.of(accessToken, "nickname")))
                .thenReturn(MemberCollegeAndDepartment.of(Department.BUSINESS));
        //when
        ResultActions resultActions = mockMvc.perform(get("/api/v1/college-department")
                .header(HttpHeaders.AUTHORIZATION, accessToken)
        ).andDo(print());

        //then
        resultActions.andExpect(status().isOk())
                .andDo(document("member/collegeAndDepartment/success",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("accessToken")
                        ),
                        responseFields(
                                fieldWithPath("college").description("단과대"),
                                fieldWithPath("department").description("학과")
                        )
                ));
    }

}