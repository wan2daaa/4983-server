package team.dankookie.server4983.member.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;
import team.dankookie.server4983.common.BaseControllerTest;
import team.dankookie.server4983.common.exception.ErrorResponse;
import team.dankookie.server4983.member.dto.MemberPasswordChangeRequest;
import team.dankookie.server4983.member.service.MemberService;
import team.dankookie.server4983.sms.dto.SmsCertificationNumber;
import team.dankookie.server4983.sms.service.CoolSmsService;
import team.dankookie.server4983.sms.service.SmsService;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberPasswordControllerTest extends BaseControllerTest {

    @Test
    void 학번과_휴대폰번호를_받으면_인증번호를_리턴한다() throws Exception {
        //given
        final String studentId = "201511111";
        final String phoneNumber = "01012345678";
        final String certificationNumber = "123456";
        final SmsCertificationNumber smsCertificationNumber = SmsCertificationNumber.of(certificationNumber);

        when(memberService.isMemberExistsByMemberPasswordRequest(studentId, phoneNumber))
                .thenReturn(true);

        when(smsService.sendCertificationNumberToPhoneNumber(anyString()))
                .thenReturn(smsCertificationNumber);
        //when

        ResultActions resultActions = mockMvc.perform(get(API + "/members/password/certification-number")
                        .contentType(APPLICATION_JSON)
                        .param("studentId", studentId)
                        .param("phoneNumber", phoneNumber))
                .andDo(print());

        //then
        resultActions.andExpect(status().isOk())
                .andDo(
                        document("member/password/certification-number/success",
                                queryParameters(
                                        parameterWithName("studentId").description("학번"),
                                        parameterWithName("phoneNumber").description("휴대폰번호")
                                ),
                                responseFields(
                                        fieldWithPath("certificationNumber").description("인증번호")
                                )
                        )
                );

        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
        assertThat(content).isEqualTo(objectMapper.writeValueAsString(SmsCertificationNumber.of(certificationNumber)));
    }

    @Test
    void 학번이_올바르지_않으면_에러코드를_던진다() throws Exception {
        //given
        final String studentId = "201511111";
        final String phoneNumber = "01012345678";
        final String certificationNumber = "123456";
        final SmsCertificationNumber smsCertificationNumber = SmsCertificationNumber.of(certificationNumber);

        when(memberService.isMemberExistsByMemberPasswordRequest(studentId, phoneNumber))
                .thenThrow(new IllegalArgumentException("존재하지 않는 학번입니다."));
        //when

        ResultActions resultActions = mockMvc.perform(get(API + "/members/password/certification-number")
                        .contentType(APPLICATION_JSON)
                        .param("studentId", studentId)
                        .param("phoneNumber", phoneNumber))
                .andDo(print());

        //then
        resultActions.andExpect(status().isBadRequest())
                .andDo(
                        document("member/password/certification-number/fail/studentId",
                                queryParameters(
                                        parameterWithName("studentId").description("학번"),
                                        parameterWithName("phoneNumber").description("휴대폰번호")
                                ),
                                responseFields(
                                        fieldWithPath("message").description("에러 메시지")
                                )
                        )
                );

        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
        assertThat(content).isEqualTo(objectMapper.writeValueAsString(ErrorResponse.of("존재하지 않는 학번입니다.")));
    }

    @Test
    void 학번과_휴대폰번호가_맞지_않으면_에러코드를_던진다() throws Exception {
        //given
        final String studentId = "201511111";
        final String phoneNumber = "01012345678";
        final String certificationNumber = "123456";
        final SmsCertificationNumber smsCertificationNumber = SmsCertificationNumber.of(certificationNumber);

        when(memberService.isMemberExistsByMemberPasswordRequest(studentId, phoneNumber))
                .thenThrow(new IllegalArgumentException("학번과 맞지 않는 휴대폰번호입니다."));
        //when

        ResultActions resultActions = mockMvc.perform(get(API + "/members/password/certification-number")
                        .contentType(APPLICATION_JSON)
                        .param("studentId", studentId)
                        .param("phoneNumber", phoneNumber))
                .andDo(print());

        //then
        resultActions.andExpect(status().isBadRequest())
                .andDo(
                        document("member/password/certification-number/fail/phoneNumber",
                                queryParameters(
                                        parameterWithName("studentId").description("학번"),
                                        parameterWithName("phoneNumber").description("휴대폰번호")
                                ),
                                responseFields(
                                        fieldWithPath("message").description("에러 메시지")
                                )
                        )
                );

        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
        assertThat(content).isEqualTo(objectMapper.writeValueAsString(ErrorResponse.of("학번과 맞지 않는 휴대폰번호입니다.")));
    }

    @Test
    void 사용자의_비밀번호를_변경한다() throws Exception {
        //given
        final MemberPasswordChangeRequest request = MemberPasswordChangeRequest.of("studentId", "phoneNumber", "password");

        when(memberService.changeMemberPassword(request))
                .thenReturn(true);

        //when
        ResultActions resultActions = mockMvc.perform(patch(API + "/members/password")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print());

        //then
        resultActions.andExpect(status().isOk())
                .andDo(document("member/password/change/success",
                        requestFields(
                                fieldWithPath("studentId").description("학번"),
                                fieldWithPath("phoneNumber").description("휴대폰번호"),
                                fieldWithPath("password").description("비밀번호")
                        )
                ));
    }

    @Test
    void 비밀번호_변경시_학번이_존재하지않으면_에러를_던진다() throws Exception {
        //given
        final MemberPasswordChangeRequest request = MemberPasswordChangeRequest.of("studentId", "phoneNumber", "password");

        when(memberService.changeMemberPassword(any()))
                .thenThrow(new IllegalArgumentException("존재하지 않는 학번입니다."));

        //when
        ResultActions resultActions = mockMvc.perform(patch(API + "/members/password")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print());

        //then
        resultActions.andExpect(status().isBadRequest())
                .andDo(document("member/password/change/fail/studentId",
                        requestFields(
                                fieldWithPath("studentId").description("학번"),
                                fieldWithPath("phoneNumber").description("휴대폰번호"),
                                fieldWithPath("password").description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("message").description("에러 메시지")
                        )
                ));

        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
        assertThat(content).isEqualTo(objectMapper.writeValueAsString(ErrorResponse.of("존재하지 않는 학번입니다.")));
    }

    @Test
    void 비밀번호_변경시_학번과_휴대폰번호가_일치하지_않으면_에러를_던진다() throws Exception {
        //given
        final MemberPasswordChangeRequest request = MemberPasswordChangeRequest.of("studentId", "phoneNumber", "password");

        when(memberService.changeMemberPassword(any(MemberPasswordChangeRequest.class)))
                .thenThrow(new IllegalArgumentException("학번과 맞지 않는 휴대폰번호입니다."));

        //when
        ResultActions resultActions = mockMvc.perform(patch(API + "/members/password")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print());

        //then
        resultActions.andExpect(status().isBadRequest())
                .andDo(document("member/password/change/fail/phonenumber",
                        requestFields(
                                fieldWithPath("studentId").description("학번"),
                                fieldWithPath("phoneNumber").description("휴대폰번호"),
                                fieldWithPath("password").description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("message").description("에러 메시지")
                        )
                ));

        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
        assertThat(content).isEqualTo(objectMapper.writeValueAsString(ErrorResponse.of("학번과 맞지 않는 휴대폰번호입니다.")));
    }

}