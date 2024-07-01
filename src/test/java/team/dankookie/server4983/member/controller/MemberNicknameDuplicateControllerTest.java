package team.dankookie.server4983.member.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import team.dankookie.server4983.common.BaseControllerTest;
import team.dankookie.server4983.member.service.MemberService;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class MemberNicknameDuplicateControllerTest extends BaseControllerTest {

    @Test
    void 닉네임이_중복인경우_true를_리턴한다() throws Exception {
//       GIVEN
        final String nickname = "testNICkname";

        when(memberService.checkNicknameDuplicate(nickname))
                .thenReturn(true);
//        WHEN
        ResultActions resultActions = mockMvc.perform(get(API + "/my-pages/nicknames/duplicates")
                        .param("nickname", nickname))
                .andDo(MockMvcResultHandlers.print());

//        THEN
        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("my-pages/nickname-duplicate/success",
                        queryParameters(
                                parameterWithName("nickname").description("중복 검사할 닉네임")
                        ),
                        responseFields(
                                fieldWithPath("nicknameDuplicate").description("닉네임 중복 여부")
                        )
                ));
    }
}
