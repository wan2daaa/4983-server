package team.dankookie.server4983.book.controller;

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
import team.dankookie.server4983.book.constant.BookStatus;
import team.dankookie.server4983.book.dto.AdminUsedBookListResponse;
import team.dankookie.server4983.book.service.AdminUsedBookService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/used-book")
public class AdminUsedBookController {

    private final AdminUsedBookService adminUsedBookService;

    @GetMapping
    public Page<AdminUsedBookListResponse> getMember(Pageable pageable, @RequestParam(value = "") String searchKeyword, @RequestParam(required = false) BookStatus bookStatus) {

        return adminUsedBookService.getUsedBook(pageable, searchKeyword, bookStatus);
    }

    @PatchMapping("/{id}/{bookStatus}")
    public ResponseEntity<Void> updateBookStatus(@PathVariable Long id, @PathVariable BookStatus bookStatus) {
        adminUsedBookService.updateBookStatus(id, bookStatus);
        return ResponseEntity.ok().build();

    }

}
