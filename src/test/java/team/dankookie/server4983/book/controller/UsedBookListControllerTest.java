package team.dankookie.server4983.book.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;
import team.dankookie.server4983.book.constant.BookStatus;
import team.dankookie.server4983.book.constant.College;
import team.dankookie.server4983.book.constant.Department;
import team.dankookie.server4983.book.dto.UsedBookListResponse;
import team.dankookie.server4983.book.service.UsedBookListService;
import team.dankookie.server4983.common.BaseControllerTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UsedBookListControllerTest extends BaseControllerTest {

    @Test
    void 서적의_모든_리스트를_반환한다() throws Exception {
        //given
        final boolean isOrderByTradeAvailableDatetime = false;

        UsedBookListResponse usedBookListResponse1 = UsedBookListResponse.builder()
                .usedBookId(1L)
                .imageUrl("imageUrl")
                .bookStatus(BookStatus.SALE)
                .name("책이름")
                .tradeAvailableDatetime(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .price(10000)
                .build();

        UsedBookListResponse usedBookListResponse2 = UsedBookListResponse.builder()
                .usedBookId(2L)
                .imageUrl("imageUrl")
                .bookStatus(BookStatus.TRADE)
                .name("책이름")
                .tradeAvailableDatetime(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .price(100000)
                .build();

        final List<UsedBookListResponse> usedBookListResponseList = List.of(
                usedBookListResponse1, usedBookListResponse2
        );

        when(usedBookListService.getUsedBookList(false))
                .thenReturn(usedBookListResponseList);

        //when
        ResultActions resultActions = mockMvc.perform(get(API + "/used-book-list")
                        .queryParam("isOrderByTradeAvailableDatetime", String.valueOf(isOrderByTradeAvailableDatetime)))
                .andDo(print());

        //then
        resultActions.andExpect(status().isOk())
                .andDo(
                        document("used-book-list/getUsedBookList/success",
                                queryParameters(
                                        parameterWithName("isOrderByTradeAvailableDatetime").description("빠른 거래 날짜 순으로 정렬할지 여부")
                                ),
                                responseFields(
                                        fieldWithPath("[].usedBookId").description("책 게시글 id"),
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
    void 서적을_단과대와_학과에따라_리스트로_반환한다() throws Exception {
        //given
        final boolean isOrderByTradeAvailableDatetime = false;

        UsedBookListResponse usedBookListResponse1 = UsedBookListResponse.builder()
                .usedBookId(1L)
                .imageUrl("imageUrl")
                .bookStatus(BookStatus.SALE)
                .name("책이름")
                .tradeAvailableDatetime(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .price(10000)
                .build();

        UsedBookListResponse usedBookListResponse2 = UsedBookListResponse.builder()
                .usedBookId(2L)
                .imageUrl("imageUrl")
                .bookStatus(BookStatus.TRADE)
                .name("책이름")
                .tradeAvailableDatetime(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .price(100000)
                .build();

        final List<UsedBookListResponse> usedBookListResponseList = List.of(
                usedBookListResponse1, usedBookListResponse2
        );

        List<College> collegeList = List.of(College.ENGINEERING, College.EDUCATION);
        List<Department> departmentList = List.of(Department.COMPUTER, Department.SOFTWARE, Department.KOREAN);

        when(usedBookListService.getUsedBookList(collegeList, departmentList, isOrderByTradeAvailableDatetime))
                .thenReturn(usedBookListResponseList);

        //when
        ResultActions resultActions = mockMvc.perform(get(API + "/used-book-list/college-and-department")
                        .param("college", "ENGINEERING,EDUCATION")
                        .param("department", "COMPUTER,SOFTWARE,KOREAN")
                        .param("isOrderByTradeAvailableDatetime", String.valueOf(isOrderByTradeAvailableDatetime))
                )
                .andDo(print());

        //then
        resultActions.andExpect(status().isOk())
                .andDo(
                        document("used-book-list/getUsedBookListWithCollegeAndDepartment/success",
                                queryParameters(
                                        parameterWithName("college").description("단과대학 enum 리스트"),
                                        parameterWithName("department").description("학과 enum 리스트"),
                                        parameterWithName("isOrderByTradeAvailableDatetime").description("빠른 거래 날짜 순으로 정렬할지 여부")
                                ),
                                responseFields(
                                        fieldWithPath("[].usedBookId").description("책 게시글 id"),
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
    void 서적을_키워드로_검색을_한다() throws Exception {
        //given
        final boolean isOrderByTradeAvailableDatetime = false;

        UsedBookListResponse usedBookListResponse1 = UsedBookListResponse.builder()
                .usedBookId(1L)
                .imageUrl("imageUrl")
                .bookStatus(BookStatus.SALE)
                .name("책이름")
                .tradeAvailableDatetime(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .price(10000)
                .build();

        UsedBookListResponse usedBookListResponse2 = UsedBookListResponse.builder()
                .usedBookId(2L)
                .imageUrl("imageUrl")
                .bookStatus(BookStatus.TRADE)
                .name("책이름")
                .tradeAvailableDatetime(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .price(100000)
                .build();

        final List<UsedBookListResponse> usedBookListResponseList = List.of(
                usedBookListResponse1, usedBookListResponse2
        );

        when(usedBookListService.getUsedBookListBySearchKeyword("책이름", isOrderByTradeAvailableDatetime))
                .thenReturn(usedBookListResponseList);

        //when
        ResultActions resultActions = mockMvc.perform(get(API + "/used-book-list/search")
                        .queryParam("isOrderByTradeAvailableDatetime", String.valueOf(isOrderByTradeAvailableDatetime))
                        .queryParam("searchKeyword", "책이름"))
                .andDo(print());

        //then
        resultActions.andExpect(status().isOk())
                .andDo(
                        document("used-book-list/getUsedBookList/search/success",
                                queryParameters(
                                        parameterWithName("isOrderByTradeAvailableDatetime").description("빠른 거래 날짜 순으로 정렬할지 여부"),
                                        parameterWithName("searchKeyword").description("검색할 이름")
                                ),
                                responseFields(
                                        fieldWithPath("[].usedBookId").description("책 게시글 id"),
                                        fieldWithPath("[].imageUrl").description("책 이미지"),
                                        fieldWithPath("[].bookStatus").description("책 상태"),
                                        fieldWithPath("[].name").description("책 이름"),
                                        fieldWithPath("[].tradeAvailableDatetime").description("거래 가능 날짜"),
                                        fieldWithPath("[].createdAt").description("책 등록 날짜"),
                                        fieldWithPath("[].price").description("책 가격")
                                )
                        ));
    }


}