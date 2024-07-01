package team.dankookie.server4983.book.dto;

import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDate;

import lombok.Getter;

@Getter
public class AdminLockerListResponse {

    private final Integer lockerNumber;
    private final String SellerStudentId;
    private final Boolean isExists;
    private final String buyerStudentId;
    private final LocalDate tradeAvailableDate;

    @QueryProjection
    public AdminLockerListResponse(Integer lockerNumber, String sellerStudentId, Boolean isExists,
                                   String buyerStudentId, LocalDate tradeAvailableDate) {
        this.lockerNumber = lockerNumber;
        SellerStudentId = sellerStudentId;
        this.isExists = isExists;
        this.buyerStudentId = buyerStudentId;
        this.tradeAvailableDate = tradeAvailableDate;
    }

    public static AdminLockerListResponse of(Integer lockerNumber, String sellerStudentId, Boolean isExists,
                                             String buyerStudentId, LocalDate tradeAvailableDate) {
        return new AdminLockerListResponse(lockerNumber, sellerStudentId, isExists, buyerStudentId, tradeAvailableDate);
    }
}
