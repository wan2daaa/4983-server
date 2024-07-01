package team.dankookie.server4983.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.dankookie.server4983.member.dto.MemberPasswordChangeRequest;
import team.dankookie.server4983.member.service.MemberService;
import team.dankookie.server4983.sms.dto.SmsCertificationNumber;
import team.dankookie.server4983.sms.service.CoolSmsService;
import team.dankookie.server4983.sms.service.SmsService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/members")
public class MemberPasswordController {

    private final MemberService memberService;
    private final CoolSmsService smsService;

    @GetMapping("/password/certification-number")
    public ResponseEntity<SmsCertificationNumber> getCertificationNumber(@RequestParam final String studentId, @RequestParam final String phoneNumber) {
        memberService.isMemberExistsByMemberPasswordRequest(studentId, phoneNumber);

        SmsCertificationNumber certificationNumber = smsService.sendCertificationNumberToPhoneNumber(
                phoneNumber);
        return ResponseEntity.ok(certificationNumber);
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(@RequestBody MemberPasswordChangeRequest request) {
        boolean isChanged = memberService.changeMemberPassword(request);
        return ResponseEntity.ok().build();
    }

}
