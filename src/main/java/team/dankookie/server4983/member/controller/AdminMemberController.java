package team.dankookie.server4983.member.controller;

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
import team.dankookie.server4983.member.dto.AdminMemberListResponse;
import team.dankookie.server4983.member.service.AdminMemberService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/member")
public class AdminMemberController {

    private final AdminMemberService adminMemberService;


    @GetMapping
    public Page<AdminMemberListResponse> getMember(Pageable pageable, @RequestParam(value = "") String searchKeyword) {
        return adminMemberService.getMember(pageable, searchKeyword);
    }

    @GetMapping("/block")
    public Page<AdminMemberListResponse> getBlockedMember(Pageable pageable, @RequestParam(value = "") String searchKeyword) {
        return adminMemberService.getBlockedMember(pageable, searchKeyword);
    }


    @PatchMapping("/block/{id}")
    public ResponseEntity<Boolean> blockMember(@PathVariable Long id) {
        return ResponseEntity.ok(adminMemberService.blockMember(id));
    }

}
