package team.dankookie.server4983.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.dankookie.server4983.book.constant.BookStatus;
import team.dankookie.server4983.book.dto.UsedBookListResponse;
import team.dankookie.server4983.book.repository.mypageBookPurchaseDetail.BookPurchaseDetailRepository;
import team.dankookie.server4983.jwt.dto.AccessToken;
import team.dankookie.server4983.member.domain.Member;
import team.dankookie.server4983.member.repository.MemberRepository;

import java.util.List;


@RequiredArgsConstructor
@Service

public class MyPageBookPurchaseDetailListService {
    private final BookPurchaseDetailRepository bookPurchaseDetailRepository;
    private final MemberRepository memberRepository;

    public List<UsedBookListResponse> getMyPageBookPurchaseDetailList(BookStatus bookStatus, AccessToken accessToken) {

        String nickname = accessToken.nickname();

        Member member = memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        return bookPurchaseDetailRepository.getMyPageBookPurchaseDetailList(bookStatus, member.getId());
    }
}