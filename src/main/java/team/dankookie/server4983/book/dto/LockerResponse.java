package team.dankookie.server4983.book.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class LockerResponse {

    private final Integer lockerNumber;
    private final Boolean isExists;

    @QueryProjection
    public LockerResponse(Integer lockerNumber, Boolean isExists) {
        this.lockerNumber = lockerNumber;
        this.isExists = isExists;
    }

    public static LockerResponse of(Integer lockerNumber, Boolean isExists) {
        return new LockerResponse(lockerNumber, isExists);
    }
}
