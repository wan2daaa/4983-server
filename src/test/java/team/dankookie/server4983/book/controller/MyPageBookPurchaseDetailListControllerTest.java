package team.dankookie.server4983.book.controller;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.test.web.servlet.ResultActions;
import team.dankookie.server4983.book.constant.BookStatus;
import team.dankookie.server4983.book.dto.UsedBookListResponse;
import team.dankookie.server4983.book.service.MyPageBookPurchaseDetailListService;
import team.dankookie.server4983.common.BaseControllerTest;
import team.dankookie.server4983.common.exception.ErrorResponse;
import team.dankookie.server4983.jwt.constants.TokenDuration;
import team.dankookie.server4983.jwt.dto.AccessToken;

import java.time.LocalDateTime;
import java.util.List;

import static io.jsonwebtoken.lang.Strings.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

class MyPageBookPurchaseDetailListControllerTest extends BaseControllerTest {

    @Test
    void 구매내역_서적리스트를_리턴한다() throws Exception {
        BookStatus bookStatus = BookStatus.SOLD;
        String accessToken = jwtTokenUtils.generateJwtToken("nickname", TokenDuration.ACCESS_TOKEN_DURATION.getDuration());

        UsedBookListResponse usedBookListResponse1 = UsedBookListResponse.builder()
                .usedBookId(1L)
                .imageUrl("imageUrl")
                .bookStatus(BookStatus.SOLD)
                .name("책이름")
                .tradeAvailableDatetime(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .price(10000)
                .build();

        UsedBookListResponse usedBookListResponse2 = UsedBookListResponse.builder()
                .usedBookId(2L)
                .imageUrl("imageUrl1")
                .bookStatus(BookStatus.SOLD)
                .name("책이름1")
                .tradeAvailableDatetime(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .price(20000)
                .build();

        final List<UsedBookListResponse> myPageBookPurchaseDetailListResponse = List.of(
                usedBookListResponse1, usedBookListResponse2
        );

        when(myPageBookPurchaseDetailListService.getMyPageBookPurchaseDetailList(bookStatus, AccessToken.of(accessToken, "nickname")))
                .thenReturn(myPageBookPurchaseDetailListResponse);

        //when
        ResultActions resultActions = mockMvc.perform(get(API + "/my-pages/book-purchase-detail-list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("bookStatus", String.valueOf(BookStatus.SOLD))
                        .header("Authorization", accessToken))
                .andDo(print());
        //then
        resultActions.andExpect(status().isOk())
                .andDo(
                        document("my-pages/book-purchase-detail-list/success",
                                queryParameters(
                                        parameterWithName("bookStatus").description("거래 완료 구분자")
                                ),
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION).description("accessToken")
                                ),
                                responseFields(
                                        fieldWithPath("[].usedBookId").description("책 게시물 id"),
                                        fieldWithPath("[].imageUrl").description("책 이미지"),
                                        fieldWithPath("[].bookStatus").description("책 상태"),
                                        fieldWithPath("[].name").description("책 이름"),
                                        fieldWithPath("[].tradeAvailableDatetime").description("거래 가능 날짜"),
                                        fieldWithPath("[].createdAt").description("책 등록 날짜"),
                                        fieldWithPath("[].price").description("책 가격")
                                )
                        ));
    }

    @Test
    void 구매내역이_없는경우_빈리스트를_리턴한다() throws Exception {
        //given
        BookStatus bookStatus = BookStatus.SOLD;
        String accessToken = jwtTokenUtils.generateJwtToken("nickname", TokenDuration.ACCESS_TOKEN_DURATION.getDuration());

        UsedBookListResponse usedBookListResponse = UsedBookListResponse.builder().build();

        final List<UsedBookListResponse> result = List.of(
                usedBookListResponse
        );

        when(myPageBookPurchaseDetailListService.getMyPageBookPurchaseDetailList(bookStatus, AccessToken.of(accessToken, "nickname")))
                .thenReturn(result);
        //when
        ResultActions resultActions = mockMvc.perform(get(API + "/my-pages/book-purchase-detail-list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("bookStatus", String.valueOf(BookStatus.SOLD))
                        .header("Authorization", accessToken))
                .andDo(print());
        //then
        resultActions.andExpect(status().isOk())
                .andDo(
                        document("my-pages/book-purchase-detail-list/empty",
                                queryParameters(
                                        parameterWithName("bookStatus").description("거래 완료 구분자")
                                ),
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION).description("accessToken")
                                )
                        ));
    }

    @Test
    void 회원이_존재하지_않는경우_에러를_리턴한다() throws Exception {
        //given
        String accessToken = jwtTokenUtils.generateJwtToken("nickname", TokenDuration.ACCESS_TOKEN_DURATION.getDuration());
        final BookStatus bookstatus = BookStatus.SOLD;

        when(myPageBookPurchaseDetailListService.getMyPageBookPurchaseDetailList(bookstatus, AccessToken.of(accessToken, "nickname")))
                .thenThrow(new IllegalArgumentException("존재하지 않는 회원입니다."));
        //when
        ResultActions resultActions = mockMvc.perform(get(API + "/my-pages/book-purchase-detail-list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("bookStatus", String.valueOf(BookStatus.SOLD))
                        .header("Authorization", accessToken))
                .andDo(print());
        //then
        resultActions.andExpect(status().isBadRequest())
                .andDo(
                        document("my-pages/book-purchase-detail-list/fail",
                                RequestDocumentation.queryParameters(
                                        parameterWithName("bookStatus").description("거래완료 구분자")
                                ),
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION).description("accessToken")
                                ),
                                responseFields(
                                        fieldWithPath("message").description("에러 메시지")
                                )
                        )
                );
        String contentAsString = resultActions.andReturn().getResponse().getContentAsString(UTF_8);
        assertThat(contentAsString).isEqualTo(objectMapper.writeValueAsString(ErrorResponse.of("존재하지 않는 회원입니다.")));

    }

}