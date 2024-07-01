package team.dankookie.server4983.member.controller;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import team.dankookie.server4983.common.BaseControllerTest;
import team.dankookie.server4983.jwt.constants.TokenDuration;
import team.dankookie.server4983.jwt.dto.AccessToken;
import team.dankookie.server4983.member.constant.AccountBank;
import team.dankookie.server4983.member.dto.MemberImageRequest;
import team.dankookie.server4983.member.dto.MemberProfileSaveRequest;
import team.dankookie.server4983.member.dto.MemberProfileSaveResponse;
import team.dankookie.server4983.member.service.MemberService;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberUpdateControllerTest extends BaseControllerTest {

    @Test
    void 마이페이지_프로필을_수정한다() throws Exception {
        //given
        final Long memberId = 1L;

        final MemberProfileSaveRequest memberProfileSaveRequest = MemberProfileSaveRequest.of(
                "김민진",
                AccountBank.KB,
                "938002-00-613983"
        );

        String accessToken = jwtTokenUtils.generateJwtToken("nickname", TokenDuration.ACCESS_TOKEN_DURATION.getDuration());

        MockMultipartFile fileImage = new MockMultipartFile("fileImage", "fileImage.png", MediaType.MULTIPART_FORM_DATA_VALUE, "fileImage".getBytes(UTF_8));
        MockMultipartFile member = new MockMultipartFile("member", null, MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(memberProfileSaveRequest).getBytes(UTF_8));
        when(memberService.updateMemberProfile(any(), any(MemberProfileSaveRequest.class), any(AccessToken.class)))
                .thenReturn(MemberProfileSaveResponse.of(memberId));

        //when
        ResultActions resultActions = mockMvc.perform(multipart(API + "/my-pages/update")
                .file(fileImage)
                .file(member)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
        ).andDo(print());
        //then
        resultActions.andExpect(status().isOk())
                .andDo(document("my-pages/update/success",
                                requestHeaders(
                                        headerWithName(org.springframework.http.HttpHeaders.AUTHORIZATION).description("accessToken")
                                ),
                                requestParts(
                                        partWithName("fileImage").description("프로필에 저장할 이미지"),
                                        partWithName("member").description("수정할 회원 정보")
                                ),
                                requestPartFields(
                                        "member",
                                        fieldWithPath("nickname").description("수정할 닉네임"),
                                        fieldWithPath("accountBank").description("수정할 은행계좌"),
                                        fieldWithPath("accountNumber").description("수정할 계좌번호")
                                )
                        )
                );
    }

    @Test
    void 마이페이지_프로필_이미지를_삭제한다() throws Exception {
        //given
        MemberImageRequest request = MemberImageRequest.of("https://s3.4983.com/imageUrl.png");
        String accessToken = jwtTokenUtils.generateJwtToken("nickname", TokenDuration.ACCESS_TOKEN_DURATION.getDuration());

        //when
        ResultActions resultActions = mockMvc.perform(delete(API + "/my-pages/delete/image")
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"imageUrl\":\"" + request.imageUrl() + "\"}"))
                .andDo(print());
        //then
        resultActions.andExpect(status().isNoContent())
                .andDo(document("my-pages/image/delete/success",
                        requestFields(
                                fieldWithPath("imageUrl").description("삭제할 이미지")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("accessToken")
                        )
                ));
    }

}