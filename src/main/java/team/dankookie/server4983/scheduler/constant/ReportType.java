package team.dankookie.server4983.scheduler.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportType {

    NOT_BOOK_ARRANGEMENT("NOT_NOOK_ARRANGEMENT", "판매자가 도서를 배치하지 않았습니다."),
    NOT_DEPOSIT("NOT_DEPOSIT", "구매자가 아직 입금하지 않았습니다."),
    NOT_BOOK_PICKUP("NOT_BOOK_PICKUP", "구매자가 서적 수령을 하지 않았습니다.");

    private final String type;
    private final String message;


}
