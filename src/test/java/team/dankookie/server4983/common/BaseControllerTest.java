package team.dankookie.server4983.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import team.dankookie.server4983.book.service.*;
import team.dankookie.server4983.chat.service.ChatService;
import team.dankookie.server4983.jwt.constants.TokenSecretKey;
import team.dankookie.server4983.jwt.service.RefreshTokenService;
import team.dankookie.server4983.jwt.util.JwtTokenUtils;
import team.dankookie.server4983.member.service.MemberService;
import team.dankookie.server4983.sms.service.CoolSmsService;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;


@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
public abstract class BaseControllerTest extends BaseDisplayNameConfig {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JwtTokenUtils jwtTokenUtils;

    @Autowired
    protected TokenSecretKey tokenSecretKey;

    protected final String API = "/api/v1";

    @BeforeEach
    public void init(WebApplicationContext webApplicationContext,
                     RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }


    @MockBean
    protected LockerService lockerService;

    @MockBean
    protected MyPageBookPurchaseDetailListService myPageBookPurchaseDetailListService;

    @MockBean
    protected MyPageBookSalesDetailListService myPageBookSalesDetailListService;

    @MockBean
    protected UsedBookService usedBookService;

    @MockBean
    protected UsedBookListService usedBookListService;

    @MockBean
    protected ChatService chatService;

    @MockBean
    protected MemberService memberService;

    @MockBean
    protected RefreshTokenService refreshTokenService;

    @MockBean
    protected CoolSmsService smsService;
}