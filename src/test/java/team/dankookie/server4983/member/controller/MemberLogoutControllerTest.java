//package team.dankookie.server4983.member.controller;
//
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.restdocs.cookies.CookieDocumentation;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import team.dankookie.server4983.common.BaseControllerTest;
//
//
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//class MemberLogoutControllerTest {
//
//    @InjectMocks
//    private MemberLogoutController logoutController;
//
//    @MockBean
//    private HttpServletRequest request;
//
//    @MockBean
//    private HttpServletResponse response;
//
//    @Test
//    void 로그아웃시_refreshToken을_삭제한다() throws Exception {
//        //given
//        String logoutUrl = API + "/logout";
//
//        Cookie refreshTokenCookie = new Cookie("refreshToken", "example_refresh_token");
//        when(request.getCookies()).thenReturn(new Cookie[]{refreshTokenCookie});
//
//        ResponseEntity<Void> responseEntity = logoutController.logout(response, refreshTokenCookie);
//
//        verify(response).addCookie(refreshTokenCookie);
//        assert (responseEntity.getStatusCodeValue() == 200);
//        //when
//        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(logoutUrl)
//                        .cookie(refreshTokenCookie)
//                        .contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andDo(print());
//
//        // Then
//        resultActions
//                .andExpect(status().isOk())
//                .andDo(document("my-pages/logout/success",
//                        CookieDocumentation.requestCookies(
//                                CookieDocumentation.cookieWithName("refreshToken").description("refreshToken")
//                        )
//                ));
//    }
//
//    @Test
//    void refreshToken이_없는상태에서_로그아웃시_null값을_반환한다(){
//        when(request.getCookies()).thenReturn(null);
//        ResponseEntity<Void> responseEntity = logoutController.logout(response, null);
//
//        assert(responseEntity.getStatusCodeValue() == 200);
//    }
//}