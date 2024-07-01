package team.dankookie.server4983.member.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;
import team.dankookie.server4983.book.constant.Department;
import team.dankookie.server4983.common.BaseControllerTest;
import team.dankookie.server4983.member.constant.AccountBank;
import team.dankookie.server4983.member.domain.Member;
import team.dankookie.server4983.member.dto.MemberRegisterRequest;
import team.dankookie.server4983.member.service.MemberService;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberRegisterControllerTest extends BaseControllerTest {

    @Test
    void 학번이_중복이_아니면_200을_리턴한다() throws Exception {
        //given
        final String studentId = "202023604";

        when(memberService.isStudentIdDuplicate(studentId))
                .thenReturn(false);
        //when
        ResultActions resultActions = mockMvc.perform(get(API + "/register/duplicate/studentId")
                        .param("studentId", studentId))
                .andDo(print());

        //then
        resultActions.andExpect(status().isOk())
                .andDo(document("member-register/duplicate/studentId/success",
                        queryParameters(
                                parameterWithName("studentId").description("학번")
                        )
                ));
    }

    @Test
    void 학번이_중복이면_400_에러와_에러메시지를_리턴한다() throws Exception {
        //given
        final String studentId = "202023604";

        when(memberService.isStudentIdDuplicate(studentId))
                .thenReturn(true);
        //when
        ResultActions resultActions = mockMvc.perform(get(API + "/register/duplicate/studentId")
                        .param("studentId", studentId))
                .andDo(print());

        //then
        resultActions.andExpect(status().isBadRequest())
                .andDo(document("member-register/duplicate/studentId/fail",
                        queryParameters(
                                parameterWithName("studentId").description("학번")
                        ),
                        responseFields(
                                fieldWithPath("message").description("에러 메시지")
                        )
                ));
    }

    @Test
    void 닉네임이_중복이_아니면_200과_메시지를_리턴한다() throws Exception {
        //given
        final String nickname = "testNickname";

        when(memberService.isNicknameDuplicate(nickname))
                .thenReturn(false);
        //when
        ResultActions resultActions = mockMvc.perform(get(API + "/register/duplicate/nickname")
                        .param("nickname", nickname))
                .andDo(print());

        //then
        resultActions.andExpect(status().isOk())
                .andDo(document("member-register/duplicate/nickname/success",
                        queryParameters(
                                parameterWithName("nickname").description("닉네임")
                        ),
                        responseFields(
                                fieldWithPath("message").description("성공 메시지")
                        )
                ));
    }

    @Test
    void 닉네임이_중복이면_400과_에러메시지를_리턴한다() throws Exception {
        //given
        final String nickname = "testNickname";

        when(memberService.isNicknameDuplicate(nickname))
                .thenReturn(true);
        //when
        ResultActions resultActions = mockMvc.perform(get(API + "/register/duplicate/nickname")
                        .param("nickname", nickname))
                .andDo(print());

        //then
        resultActions.andExpect(status().isBadRequest())
                .andDo(document("member-register/duplicate/nickname/fail",
                        queryParameters(
                                parameterWithName("nickname").description("닉네임")
                        ),
                        responseFields(
                                fieldWithPath("message").description("에러 메시지")
                        )
                ));
    }

    @Test
    void 멤버를_저장한다() throws Exception {
        //given
        MemberRegisterRequest request = MemberRegisterRequest.of(
                "202023604",
                Department.COMPUTER,
                2020,
                "testNickname",
                "testPassword",
                "010-1234-5678",
                true,
                "testAccountHolder",
                AccountBank.KB,
                "1234567890",
                "firebaseToken"
        );

        when(memberService.register(request))
                .thenReturn(Member.builder().build());

        //when
        ResultActions resultActions = mockMvc.perform(post(API + "/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print());

        //then
        resultActions.andExpect(status().isOk())
                .andDo(document("member-register/success",
                        requestFields(
                                fieldWithPath("studentId").description("학번"),
                                fieldWithPath("department").description("학과"),
                                fieldWithPath("yearOfAdmission").description("입학년도"),
                                fieldWithPath("nickname").description("닉네임"),
                                fieldWithPath("password").description("비밀번호"),
                                fieldWithPath("phoneNumber").description("전화번호"),
                                fieldWithPath("marketingAgree").description("마케팅 동의 여부"),
                                fieldWithPath("accountHolder").description("계좌주"),
                                fieldWithPath("accountBank").description("은행"),
                                fieldWithPath("accountNumber").description("계좌번호"),
                                fieldWithPath("firebaseToken").description("firebase Token")
                        )
                ));
    }

}