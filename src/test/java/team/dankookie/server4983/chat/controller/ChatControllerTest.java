package team.dankookie.server4983.chat.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static team.dankookie.server4983.chat.constant.ContentType.BOOK_PURCHASE_REQUEST;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import team.dankookie.server4983.chat.constant.ContentType;
import team.dankookie.server4983.chat.dto.ChatMessageResponse;
import team.dankookie.server4983.chat.dto.ChatRequest;
import team.dankookie.server4983.chat.dto.ChatRoomRequest;
import team.dankookie.server4983.chat.dto.ChatRoomResponse;
import team.dankookie.server4983.chat.dto.ChatStopRequest;
import team.dankookie.server4983.chat.service.ChatService;
import team.dankookie.server4983.common.BaseControllerTest;
import team.dankookie.server4983.jwt.constants.TokenDuration;
import team.dankookie.server4983.jwt.dto.AccessToken;

class ChatControllerTest extends BaseControllerTest {

    @Test
    void 채팅방을_생성한다() throws Exception {
        //given
        final long usedBookId = 1L;
        final String accessToken = jwtTokenUtils.generateJwtToken("nickname",
                TokenDuration.ACCESS_TOKEN_DURATION.getDuration());

        final ChatRoomRequest chatRoomRequest = ChatRoomRequest.of(usedBookId);

        when(chatService.createChatRoom(any(ChatRoomRequest.class), any(AccessToken.class)))
                .thenReturn(ChatRoomResponse.of(1L));

        //when
        ResultActions resultActions = mockMvc.perform(post(API + "/chat-room")
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chatRoomRequest))
        ).andDo(print());

        //then
        resultActions.andExpect(status().isCreated())
                .andDo(
                        document("chat/create-chat-room/success",
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION).description("accessToken")
                                ),
                                requestFields(
                                        fieldWithPath("usedBookId").description("중고서적 id")
                                ),
                                responseFields(
                                        fieldWithPath("chatRoomId").description("채팅방 id")
                                )
                        ));
    }

    @Test
    void 유저의_채팅방id에_해당하는_채팅들를_리턴한다() throws Exception {
        //given
        final long chatRoomId = 1L;
        final String accessToken = jwtTokenUtils.generateJwtToken("nickname",
                TokenDuration.ACCESS_TOKEN_DURATION.getDuration());

        ChatMessageResponse response1 = ChatMessageResponse.of(
                chatRoomId,
                "test1",
                ContentType.BOOK_PURCHASE_START,
                LocalDateTime.of(2023, 9, 22, 12, 30, 12)
        );
        ChatMessageResponse response2 = ChatMessageResponse.of(
                chatRoomId,
                "test1",
                ContentType.BOOK_PURCHASE_START,
                LocalDateTime.of(2023, 9, 22, 12, 30, 12)
        );

        when(chatService.getChattingData(anyLong(), any(AccessToken.class)))
                .thenReturn(List.of(response1, response2));

        //when
        ResultActions resultActions = mockMvc.perform(
                        RestDocumentationRequestBuilders.get(API + "/chat-room/{chatRoomId}", chatRoomId)
                                .header(HttpHeaders.AUTHORIZATION, accessToken))
                .andDo(print());

        //then
        resultActions.andExpect(status().isOk())
                .andDo(document(
                        "chat/get-chatting-data/success",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("accessToken")
                        ),
                        pathParameters(
                                parameterWithName("chatRoomId").description("채팅방 id")
                        ),
                        responseFields(
                                fieldWithPath("[].chatRoomId").description("채팅방 id"),
                                fieldWithPath("[].message").description("채팅 메시지"),
                                fieldWithPath("[].contentType").description("채팅의 타입"),
                                fieldWithPath("[].createdAt").description("채팅을 보낸 시간")
                        )
                ));

    }

    @Test
    void 채팅방에서_버튼클릭시_모든_이벤트를_여기서_처리한다() throws Exception {
        //given
        final ChatRequest chatRequest = ChatRequest.of(1L, BOOK_PURCHASE_REQUEST);

        final String accessToken = jwtTokenUtils.generateJwtToken("nickname",
                TokenDuration.ACCESS_TOKEN_DURATION.getDuration());

        ChatMessageResponse response = ChatMessageResponse.of(
                1L,
                "누구누구님이 거래 요청을 보넀어요! \n오늘 거래하러 갈래요?",
                ContentType.BOOK_PURCHASE_REQUEST_SELLER,
                LocalDateTime.of(2023, 9, 22, 12, 30, 12)
        );

        when(chatService.chatRequestHandler(any(ChatRequest.class), any(AccessToken.class)))
                .thenReturn(List.of(response));
        //when
        ResultActions resultActions = mockMvc.perform(post(API + "/chat-room/interact")
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chatRequest)))
                .andDo(print());

        //then
        resultActions.andExpect(status().isCreated())
                .andDo(
                        document("chat-room/interact/success",
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION).description("accessToken")
                                ),
                                requestFields(
                                        fieldWithPath("chatRoomId").description("채팅방 id"),
                                        fieldWithPath("contentType").description("채팅 타입"),
                                        fieldWithPath("message").description(
                                                "보낼 메시지 -> 메시지를 따로 보내는게 아니면, null을 담아주시면 됩니다")
                                ),
                                responseFields(
                                        fieldWithPath("[].chatRoomId").description("채팅방 id"),
                                        fieldWithPath("[].message").description("메시지"),
                                        fieldWithPath("[].contentType").description("채팅 타입"),
                                        fieldWithPath("[].createdAt").description("채팅을 보낸 시간")
                                )
                        )
                );
    }

    @Test
    void 읽지않은_채팅내역들을_반환해준다() throws Exception {
        //given
        final long chatRoomId = 1L;
        final String accessToken = jwtTokenUtils.generateJwtToken("nickname",
                TokenDuration.ACCESS_TOKEN_DURATION.getDuration());

        ChatMessageResponse response = ChatMessageResponse.of(
                1L,
                "누구누구님이 거래 요청을 보넀어요! \n오늘 거래하러 갈래요?",
                ContentType.BOOK_PURCHASE_REQUEST_SELLER,
                LocalDateTime.of(2023, 9, 22, 12, 30, 12)
        );

        when(chatService.getNotReadChattingData(anyLong(),
                any(AccessToken.class)))
                .thenReturn(
                        List.of(response)
                );
        //when
        ResultActions resultActions = mockMvc.perform(
                        get(API + "/chat-room/not-read/{chatRoomId}", chatRoomId)
                                .header(HttpHeaders.AUTHORIZATION, accessToken))
                .andDo(print());

        //then
        resultActions.andExpect(status().isOk())
                .andDo(
                        document("chat-room/not-read/success",
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION).description("accessToken")
                                ),
                                pathParameters(
                                        parameterWithName("chatRoomId").description("채팅방 id")
                                ),
                                responseFields(
                                        fieldWithPath("[].chatRoomId").description("채팅방 id"),
                                        fieldWithPath("[].message").description("메시지"),
                                        fieldWithPath("[].contentType").description("채팅 타입"),
                                        fieldWithPath("[].createdAt").description("채팅을 보낸 시간")
                                )
                        )
                );
    }


    @Test
    void 채팅을_정지한다() throws Exception {
        //given
        ChatStopRequest request = ChatStopRequest.builder().chatRoomId(1L).build();

        final String accessToken = jwtTokenUtils.generateJwtToken("nickname",
                TokenDuration.ACCESS_TOKEN_DURATION.getDuration());

        //when
        ResultActions resultActions = mockMvc.perform(post(API + "/chat-room/stop")
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print());

        //then
        resultActions.andExpect(status().isOk())
                .andDo(
                        document("chat-room/stop/success",
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION).description("accessToken")
                                ),
                                requestFields(
                                        fieldWithPath("chatRoomId").description("채팅방 id")
                                )
                        ));

    }

}