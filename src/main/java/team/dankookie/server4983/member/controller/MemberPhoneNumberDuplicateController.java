package team.dankookie.server4983.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.dankookie.server4983.jwt.dto.AccessToken;
import team.dankookie.server4983.member.dto.PhoneNumberDuplicateResponse;
import team.dankookie.server4983.member.service.MemberService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/my-pages")
public class MemberPhoneNumberDuplicateController {

    private final MemberService memberService;

    @GetMapping("/phoneNumber/duplicate")
    public ResponseEntity<PhoneNumberDuplicateResponse> checkPhoneNumberDuplicate(@RequestParam String phoneNumber, AccessToken accessToken) {
        boolean isDuplicate = memberService.checkPhoneNumberDuplicate(phoneNumber, accessToken.nickname());

        return ResponseEntity.ok(PhoneNumberDuplicateResponse.of(isDuplicate));
    }
}
