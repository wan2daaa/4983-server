package team.dankookie.server4983.book.repository.locker;

import java.time.LocalDate;
import java.util.List;

import team.dankookie.server4983.book.dto.AdminLockerListResponse;
import team.dankookie.server4983.book.dto.LockerResponse;

public interface LockerRepositoryCustom {

    List<LockerResponse> findByTradeDateBetweenAndIsExistsTrue(LocalDate startDate, LocalDate endDate);

    List<AdminLockerListResponse> getAdminLockerList(String searchKeyword);
}
