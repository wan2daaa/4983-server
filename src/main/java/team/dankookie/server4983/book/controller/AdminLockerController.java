package team.dankookie.server4983.book.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.dankookie.server4983.book.dto.AdminLockerListResponse;
import team.dankookie.server4983.book.service.AdminLockerService;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/locker")
public class AdminLockerController {

    private final AdminLockerService adminLockerService;

    @GetMapping
    public List<AdminLockerListResponse> getLocker(@RequestParam(value = "") String searchKeyword,
                                                   @RequestParam Boolean isExists) {

        return adminLockerService.getLocker(searchKeyword, isExists);
    }

    @PatchMapping("/{lockerNumber}/{isExists}")
    public ResponseEntity<Void> updateLocker(
            @PathVariable Integer lockerNumber,
            @PathVariable Boolean isExists
    ) {

        adminLockerService.updateLocker(lockerNumber, isExists);

        return ResponseEntity.ok().build();
    }

}
