package team.dankookie.server4983.member.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.dankookie.server4983.member.service.MemberService;
import team.dankookie.server4983.sms.dto.SmsCertificationNumber;
import team.dankookie.server4983.sms.service.SmsService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/my-pages")
public class MemberPhoneNumberController {
    private final MemberService memberService;
    private final SmsService smsService;

    @GetMapping("/certification-number")
    public ResponseEntity<SmsCertificationNumber> getCertificationNumber(@RequestParam final String phoneNumber){

        Long time = System.currentTimeMillis();
        SmsCertificationNumber certificationNumber = smsService.sendCertificationNumberToPhoneNumber(phoneNumber, time );
        return ResponseEntity.ok(certificationNumber);
    }
}