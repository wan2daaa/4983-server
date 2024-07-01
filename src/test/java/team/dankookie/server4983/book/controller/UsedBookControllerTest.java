package team.dankookie.server4983.book.controller;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;
import team.dankookie.server4983.book.constant.BookStatus;
import team.dankookie.server4983.book.constant.College;
import team.dankookie.server4983.book.constant.Department;
import team.dankookie.server4983.book.dto.UsedBookResponse;
import team.dankookie.server4983.book.dto.UsedBookSaveRequest;
import team.dankookie.server4983.book.dto.UsedBookSaveResponse;
import team.dankookie.server4983.book.service.UsedBookService;
import team.dankookie.server4983.common.BaseControllerTest;
import team.dankookie.server4983.jwt.constants.TokenDuration;
import team.dankookie.server4983.jwt.dto.AccessToken;

class UsedBookControllerTest extends BaseControllerTest {

    @Test
    void 중고서적_데이터를_저장한다() throws Exception {
        //given
        final UsedBookSaveResponse response = UsedBookSaveResponse.of(1L);

        final List<MultipartFile> multipartFileList = List.of();
        final UsedBookSaveRequest usedBookSaveRequest = UsedBookSaveRequest.of(
                College.LAW,
                Department.BUSINESS,
                15000,
                LocalDateTime.of(2023, 9, 13, 12, 0),
                "책이름",
                "출판사",
                false,
                true,
                true
        );

        String accessToken = jwtTokenUtils.generateJwtToken("nickname",
                TokenDuration.ACCESS_TOKEN_DURATION.getDuration());

        MockMultipartFile file1 = new MockMultipartFile("fileList", "file1.png",
                MediaType.MULTIPART_FORM_DATA_VALUE, "file1".getBytes(UTF_8));
        MockMultipartFile file2 = new MockMultipartFile("fileList", "file2.png",
                MediaType.MULTIPART_FORM_DATA_VALUE, "file2".getBytes(UTF_8));
        MockMultipartFile file3 = new MockMultipartFile("fileList", "file3.png",
                MediaType.MULTIPART_FORM_DATA_VALUE, "file3".getBytes(UTF_8));
        MockMultipartFile file4 = new MockMultipartFile("fileList", "file4.png",
                MediaType.MULTIPART_FORM_DATA_VALUE, "file4".getBytes(UTF_8));

        List<MockMultipartFile> fileList = List.of(file1, file2, file3, file4);
        MockMultipartFile usedBook = new MockMultipartFile("usedBook", null,
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(usedBookSaveRequest).getBytes(UTF_8));
        when(usedBookService.saveAndSaveFiles(anyList(), any(UsedBookSaveRequest.class),
                any(AccessToken.class)))
                .thenReturn(UsedBookSaveResponse.of(1L));

        //when
        ResultActions resultActions = mockMvc.perform(
                multipart(API + "/used-book")
                        .file(file1)
                        .file(file2)
                        .file(file3)
                        .file(file4)
                        .file(usedBook)
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
        ).andDo(print());

        //then
        resultActions.andExpect(status().isOk())
                .andDo(document("used-book/save/success",
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION).description("accessToken")
                                ),
                                requestParts(
                                        partWithName("fileList").description("파일 리스트"),
                                        partWithName("usedBook").description("중고서적 정보")
                                ),
                                requestPartFields(
                                        "usedBook",
                                        fieldWithPath("college").description("단과대"),
                                        fieldWithPath("department").description("학과"),
                                        fieldWithPath("price").description("가격"),
                                        fieldWithPath("tradeAvailableDatetime").description("거래 가능 날짜"),
                                        fieldWithPath("name").description("책 이름"),
                                        fieldWithPath("publisher").description("출판사"),
                                        fieldWithPath("isUnderlinedOrWrite").description("밑줄및 필기흔적 여부"),
                                        fieldWithPath("isDiscolorationAndDamage").description("페이지 변색 및 훼손 여부"),
                                        fieldWithPath("isCoverDamaged").description("겉표지 훼손 여부")
                                ),
                                responseFields(
                                        fieldWithPath("usedBookId").description("중고서적 id")
                                )
                        )
                );
    }

    @Test
    void 중고서적의_id로_중고서적_데이터를_리턴한다() throws Exception {
        //given
        String accessToken = jwtTokenUtils.generateJwtToken("nickname",
                TokenDuration.ACCESS_TOKEN_DURATION.getDuration());
        String nickname = "nickname";
        final Long usedBookId = 1L;
        final UsedBookResponse usedBookResponse = UsedBookResponse.of(
                College.LAW.name(),
                Department.BUSINESS.name(),
                "nickname",
                "profileImageUrl",
                LocalDateTime.of(2023, 9, 13, 12, 0, 0),
                List.of("image1Url", "image2Url"),
                "책이름",
                "출판사",
                LocalDateTime.of(2023, 9, 13, 12, 0),
                false,
                true,
                true,
                15000,
                BookStatus.SALE,
                true
        );

        when(usedBookService.findByUsedBookId(usedBookId, nickname))
                .thenReturn(usedBookResponse);
        //when
        ResultActions resultActions = mockMvc.perform(get(API + "/used-book/{id}", usedBookId)
                        .header(HttpHeaders.AUTHORIZATION, accessToken))
                .andDo(print());

        //then
        resultActions.andExpect(status().isOk())
                .andDo(document("used-book/detail/success",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("accessToken")
                        ),
                        pathParameters(
                                parameterWithName("id").description("중고서적 id")
                        ),
                        responseFields(
                                fieldWithPath("college").description("단과대"),
                                fieldWithPath("department").description("학과"),
                                fieldWithPath("sellerUserNickname").description("판매자 닉네임"),
                                fieldWithPath("sellerProfileImageUrl").description("판매자 프로필 이미지"),
                                fieldWithPath("createdAt").description("등록일"),
                                fieldWithPath("bookImage").description("책 이미지 주소"),
                                fieldWithPath("bookName").description("책 이름"),
                                fieldWithPath("publisher").description("출판사"),
                                fieldWithPath("tradeAvailableDatetime").description("거래 가능 날짜"),
                                fieldWithPath("price").description("가격"),
                                fieldWithPath("bookStatus").description("책 상태"),
                                fieldWithPath("underlinedOrWrite").description("밑줄및 필기흔적 여부"),
                                fieldWithPath("discolorationAndDamage").description("페이지 변색 및 훼손 여부"),
                                fieldWithPath("coverDamaged").description("겉표지 훼손 여부"),
                                fieldWithPath("isBookOwner").description("글을 올린 유저인지 여부")
                        )
                ));

    }

    @Test
    void 중고서적을_삭제한다() throws Exception {
        //given
        String accessToken = jwtTokenUtils.generateJwtToken("nickname",
                TokenDuration.ACCESS_TOKEN_DURATION.getDuration());

        final Long usedBookId = 1L;

        when(usedBookService.deleteUsedBook(usedBookId, AccessToken.of(accessToken, "nickname")))
                .thenReturn(true);
        //when
        ResultActions resultActions = mockMvc.perform(delete(API + "/used-book/{id}", usedBookId)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
        ).andDo(print());

        //then
        resultActions.andExpect(status().isNoContent())
                .andDo(document("used-book/delete/success",
                        pathParameters(
                                parameterWithName("id").description("중고서적 id")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("accessToken")
                        )
                ));
    }

    @Test
    void 중고서적을_삭제한다_실패_글을_올린_사용자가_아님() throws Exception {
        //given
        String accessToken = jwtTokenUtils.generateJwtToken("nickname",
                TokenDuration.ACCESS_TOKEN_DURATION.getDuration());

        final Long usedBookId = 1L;

        Mockito.
                when(usedBookService.deleteUsedBook(usedBookId, AccessToken.of(accessToken, "nickname")))
                .thenThrow(new IllegalArgumentException("글을 올린 사용자만 삭제할 수 있습니다."));

        //when
        ResultActions resultActions = mockMvc.perform(delete(API + "/used-book/{id}", usedBookId)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
        ).andDo(print());

        //then
        resultActions.andExpect(status().isBadRequest())
                .andDo(document("used-book/delete/fail",
                        pathParameters(
                                parameterWithName("id").description("중고서적 id")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("accessToken")
                        )
                ));
    }

    @Test
    void 중고서적의_이미지를_삭제한다() throws Exception {
        //given
        final Long usedBookId = 1L;
        final String image = "image.png";
        String accessToken = jwtTokenUtils.generateJwtToken("nickname",
                TokenDuration.ACCESS_TOKEN_DURATION.getDuration());

        when(usedBookService.deleteUsedBookImage(usedBookId, image))
                .thenReturn(true);

        //when
        ResultActions resultActions = mockMvc.perform(
                delete(API + "/used-book/{id}/image/{image}", usedBookId, image)
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
        ).andDo(print());

        //then
        resultActions.andExpect(status().isNoContent())
                .andDo(document("used-book/image/delete/success",
                        pathParameters(
                                parameterWithName("id").description("중고서적 id"),
                                parameterWithName("image").description(
                                        "https://4983-s3.s3.ap-northeast-2.amazonaws.com/ 이후의 이미지 명")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("accessToken")
                        )
                ));
    }

    @Test
    void 중고서적의_데이터를_전부_덮어씌운다() throws Exception {
        //given
        final Long usedBookId = 1L;

        final List<MultipartFile> multipartFileList = List.of();
        final UsedBookSaveRequest usedBookSaveRequest = UsedBookSaveRequest.of(
                College.LAW,
                Department.BUSINESS,
                15000,
                LocalDateTime.of(2023, 9, 13, 12, 0),
                "책이름",
                "출판사",
                false,
                true,
                true
        );

        String accessToken = jwtTokenUtils.generateJwtToken("nickname",
                TokenDuration.ACCESS_TOKEN_DURATION.getDuration());

        MockMultipartFile file1 = new MockMultipartFile("fileList", "file1.png",
                MediaType.MULTIPART_FORM_DATA_VALUE, "file1".getBytes(UTF_8));
        MockMultipartFile file2 = new MockMultipartFile("fileList", "file2.png",
                MediaType.MULTIPART_FORM_DATA_VALUE, "file2".getBytes(UTF_8));
        MockMultipartFile file3 = new MockMultipartFile("fileList", "file3.png",
                MediaType.MULTIPART_FORM_DATA_VALUE, "file3".getBytes(UTF_8));
        MockMultipartFile file4 = new MockMultipartFile("fileList", "file4.png",
                MediaType.MULTIPART_FORM_DATA_VALUE, "file4".getBytes(UTF_8));

        List<MockMultipartFile> fileList = List.of(file1, file2, file3, file4);
        MockMultipartFile usedBook = new MockMultipartFile("usedBook", null,
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(usedBookSaveRequest).getBytes(UTF_8));
        when(usedBookService.updateUsedBook(anyLong(), anyList(), any(UsedBookSaveRequest.class),
                any(AccessToken.class)))
                .thenReturn(UsedBookSaveResponse.of(usedBookId));
        //when
        ResultActions resultActions = mockMvc.perform(
                multipart(API + "/used-book/{id}", usedBookId)
                        .file(file1)
                        .file(file2)
                        .file(file3)
                        .file(file4)
                        .file(usedBook)
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
        ).andDo(print());

        //then
        resultActions.andExpect(status().isOk())
                .andDo(document("used-book/update/success",
                        pathParameters(
                                parameterWithName("id").description("중고서적 id")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("accessToken")
                        ),
                        requestParts(
                                partWithName("fileList").description("새로 저장할 이미지 리스트"),
                                partWithName("usedBook").description("중고서적 정보")
                        ),
                        requestPartFields(
                                "usedBook",
                                fieldWithPath("college").description("단과대"),
                                fieldWithPath("department").description("학과"),
                                fieldWithPath("price").description("가격"),
                                fieldWithPath("tradeAvailableDatetime").description("거래 가능 날짜"),
                                fieldWithPath("name").description("책 이름"),
                                fieldWithPath("publisher").description("출판사"),
                                fieldWithPath("isUnderlinedOrWrite").description("밑줄및 필기흔적 여부"),
                                fieldWithPath("isDiscolorationAndDamage").description("페이지 변색 및 훼손 여부"),
                                fieldWithPath("isCoverDamaged").description("겉표지 훼손 여부")
                        ),
                        responseFields(
                                fieldWithPath("usedBookId").description("중고서적 id")
                        )
                ));
    }

    @Test
    void 중고서적의_데이터를_다른_사용자가_수정하면_에러를_던진다() throws Exception {
        //given
        final Long usedBookId = 1L;

        final List<MultipartFile> multipartFileList = List.of();
        final UsedBookSaveRequest usedBookSaveRequest = UsedBookSaveRequest.of(
                College.LAW,
                Department.BUSINESS,
                15000,
                LocalDateTime.of(2023, 9, 13, 8, 0),
                "책이름",
                "출판사",
                false,
                true,
                true
        );

        String accessToken = jwtTokenUtils.generateJwtToken("nickname",
                1000L);

        MockMultipartFile file1 = new MockMultipartFile("fileList", "file1.png",
                MediaType.MULTIPART_FORM_DATA_VALUE, "file1".getBytes(UTF_8));
        MockMultipartFile file2 = new MockMultipartFile("fileList", "file2.png",
                MediaType.MULTIPART_FORM_DATA_VALUE, "file2".getBytes(UTF_8));
        MockMultipartFile file3 = new MockMultipartFile("fileList", "file3.png",
                MediaType.MULTIPART_FORM_DATA_VALUE, "file3".getBytes(UTF_8));
        MockMultipartFile file4 = new MockMultipartFile("fileList", "file4.png",
                MediaType.MULTIPART_FORM_DATA_VALUE, "file4".getBytes(UTF_8));

        List<MockMultipartFile> fileList = List.of(file1, file2, file3, file4);
        MockMultipartFile usedBook = new MockMultipartFile("usedBook", null,
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(usedBookSaveRequest).getBytes(UTF_8));
        when(usedBookService.updateUsedBook(anyLong(), anyList(), any(UsedBookSaveRequest.class),
                any(AccessToken.class)))
                .thenThrow(new IllegalArgumentException("글을 올린 유저만 수정할 수 있습니다."));
        //when
        ResultActions resultActions = mockMvc.perform(
                multipart(API + "/used-book/{id}", usedBookId)
                        .file(file1)
                        .file(file2)
                        .file(file3)
                        .file(file4)
                        .file(usedBook)
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
        ).andDo(print());

        //then
        resultActions.andExpect(status().isBadRequest())
                .andDo(document("used-book/update/fail",
                        pathParameters(
                                parameterWithName("id").description("중고서적 id")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("accessToken")
                        ),
                        requestParts(
                                partWithName("fileList").description("새로 저장할 이미지 리스트"),
                                partWithName("usedBook").description("중고서적 정보")
                        ),
                        requestPartFields(
                                "usedBook",
                                fieldWithPath("college").description("단과대"),
                                fieldWithPath("department").description("학과"),
                                fieldWithPath("price").description("가격"),
                                fieldWithPath("tradeAvailableDatetime").description("거래 가능 날짜"),
                                fieldWithPath("name").description("책 이름"),
                                fieldWithPath("publisher").description("출판사"),
                                fieldWithPath("isUnderlinedOrWrite").description("밑줄및 필기흔적 여부"),
                                fieldWithPath("isDiscolorationAndDamage").description("페이지 변색 및 훼손 여부"),
                                fieldWithPath("isCoverDamaged").description("겉표지 훼손 여부")
                        ),
                        responseFields(
                                fieldWithPath("message").description("에러 메시지")
                        )
                ));
    }


}