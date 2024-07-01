package team.dankookie.server4983.book.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.headers.HeaderDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import team.dankookie.server4983.book.dto.LockerResponse;
import team.dankookie.server4983.book.dto.LockerSaveRequest;
import team.dankookie.server4983.book.service.LockerService;
import team.dankookie.server4983.common.BaseControllerTest;
import team.dankookie.server4983.jwt.constants.TokenDuration;
import team.dankookie.server4983.jwt.constants.TokenSecretKey;
import team.dankookie.server4983.jwt.dto.AccessToken;
import team.dankookie.server4983.jwt.util.JwtTokenUtils;

class LockerControllerTest extends BaseControllerTest {

    @Test
    void 거래날짜에_책이있는_사물함번호의_리스트를_반환한다() throws Exception {
        //given
        final Long chatRoomId = 1L;

        List<LockerResponse> responseList = List.of(
                LockerResponse.of(1, true),
                LockerResponse.of(3, true),
                LockerResponse.of(5, true),
                LockerResponse.of(7, true)
        );

        when(lockerService.getExistsLockerWhenChatRoomAvailableDate(chatRoomId))
                .thenReturn(responseList);

        //when
        ResultActions resultActions = mockMvc.perform(get(API + "/locker/chat-room-available-date")
                        .param("chatRoomId", chatRoomId.toString()))
                .andDo(MockMvcResultHandlers.print());

        //then
        resultActions.andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andDo(
                document("locker/chat-room-available-date/success",
                        queryParameters(
                                parameterWithName("chatRoomId").description("채팅방 아이디")
                        ),
                        responseFields(
                                fieldWithPath("[].lockerNumber").description("사물함 번호"),
                                fieldWithPath("[].isExists").description("사물함 존재 여부")
                        )
                )
        );
    }

    @Test
    void 사물함_비밀번호와_사물함_번호_채팅방_아이디로_사물함을_예약한다() throws Exception {
        //given
        LockerSaveRequest request = LockerSaveRequest.of(1, "1234", 1L);

        final String accessToken = jwtTokenUtils.generateJwtToken("nickname",
                TokenDuration.ACCESS_TOKEN_DURATION.getDuration());

        when(lockerService.saveLocker(any(LockerSaveRequest.class), any(AccessToken.class))).thenReturn(true);
        //when
        ResultActions resultActions = mockMvc.perform(post(API + "/locker")
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(MockMvcResultHandlers.print());

        //then
        resultActions.andExpect(
                MockMvcResultMatchers.status().isCreated()
        ).andDo(
                document("locker/success",
                        requestFields(
                                fieldWithPath("lockerNumber").description("사물함 번호"),
                                fieldWithPath("password").description("사물함 비밀번호"),
                                fieldWithPath("chatRoomId").description("채팅방 아이디")
                        ),
                        HeaderDocumentation.requestHeaders(
                                HeaderDocumentation.headerWithName(HttpHeaders.AUTHORIZATION).description("accessToken")
                        ),
                        responseFields(
                                fieldWithPath("message").description("메시지")
                        )
                )
        );

    }

}