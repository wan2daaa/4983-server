package team.dankookie.server4983.chat.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;
import team.dankookie.server4983.chat.dto.ChatListResponse;
import team.dankookie.server4983.chat.service.ChatService;
import team.dankookie.server4983.common.BaseControllerTest;
import team.dankookie.server4983.jwt.constants.TokenDuration;

class ChatListControllerTest extends BaseControllerTest {

    @Test
    void accessToken으로_유저의_채팅_리스트를_리턴한다() throws Exception {
        //given
        ChatListResponse chatListResponse1 = ChatListResponse.of(1L, "사회과학통계방법", "안녕하세요", LocalDateTime.of(2023, 9, 22, 12, 30, 12), false, "imageUrl");
        ChatListResponse chatListResponse2 = ChatListResponse.of(2L, "컴퓨터공학개론", "테스트입니다.", LocalDateTime.of(2023, 5, 10, 12, 30, 12), true, "imageUrl");

        String accessToken = jwtTokenUtils.generateJwtToken("nickname", TokenDuration.ACCESS_TOKEN_DURATION.getDuration());

        when(chatService.getChatListWithAccessToken(any()))
                .thenReturn(List.of(chatListResponse1, chatListResponse2));
        //when
        ResultActions resultActions = mockMvc.perform(get(API + "/chat/list")
                        .header(HttpHeaders.AUTHORIZATION, accessToken))
                .andDo(print());

        //then
        resultActions.andExpect(status().isOk())
                .andDo(
                        document("chat-list/success",
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION).description("accessToken")
                                ),
                                responseFields(
                                        fieldWithPath("[].chatRoomId").description("채팅방 아이디"),
                                        fieldWithPath("[].usedBookName").description("채팅방 거래 책 이름"),
                                        fieldWithPath("[].message").description("채팅방의 마지막 메시지"),
                                        fieldWithPath("[].createdAt").description("채팅방의 마지막 메시지 시간"),
                                        fieldWithPath("[].isRead").description("채팅방의 읽음 여부"),
                                        fieldWithPath("[].imageUrl").description("채팅방의 이미지 url")
                                )
                        )
                );
    }


}