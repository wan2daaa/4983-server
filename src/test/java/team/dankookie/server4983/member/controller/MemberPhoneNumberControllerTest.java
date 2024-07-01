package team.dankookie.server4983.member.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;
import team.dankookie.server4983.common.BaseControllerTest;
import team.dankookie.server4983.member.service.MemberService;
import team.dankookie.server4983.sms.dto.SmsCertificationNumber;
import team.dankookie.server4983.sms.service.CoolSmsService;
import team.dankookie.server4983.sms.service.SmsService;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberPhoneNumberControllerTest extends BaseControllerTest {

    @Test
    void 휴대폰번호를_받으면_인증번호를_리턴한다() throws Exception {
        //given
        final String phoneNumber = "01012345678";
        final String certificationNumber = "123456";
        final SmsCertificationNumber smsCertificationNumber = SmsCertificationNumber.of(certificationNumber);

        when(smsService.sendCertificationNumberToPhoneNumber(anyString()))
                .thenReturn(smsCertificationNumber);
        //when
        ResultActions resultActions = mockMvc.perform(get(API + "/my-pages/certification-number")
                        .contentType(APPLICATION_JSON)
                        .param("phoneNumber", phoneNumber))
                .andDo(print());
        //then
        resultActions.andExpect(status().isOk())
                .andDo(
                        document("my-pages/certification-number/success",
                                queryParameters(
                                        parameterWithName("phoneNumber").description("휴대폰 번호")
                                ),
                                responseFields(
                                        fieldWithPath("certificationNumber").description("인증번호")
                                )
                        )
                );

        String content = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
        assertThat(content).isEqualTo(objectMapper.writeValueAsString(SmsCertificationNumber.of(certificationNumber)));
    }
}