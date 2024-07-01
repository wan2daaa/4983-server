package team.dankookie.server4983.member.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.dankookie.server4983.member.service.MemberService;
import team.dankookie.server4983.sms.dto.SmsCertificationNumber;
import team.dankookie.server4983.sms.service.CoolSmsService;
import team.dankookie.server4983.sms.service.SmsService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/my-pages")
public class MemberPhoneNumberController {
    private final CoolSmsService smsService;

    @GetMapping("/certification-number")
    public ResponseEntity<SmsCertificationNumber> getCertificationNumber(@RequestParam final String phoneNumber) {
        SmsCertificationNumber certificationNumber = smsService.sendCertificationNumberToPhoneNumber(phoneNumber);
        return ResponseEntity.ok(certificationNumber);
    }
}
