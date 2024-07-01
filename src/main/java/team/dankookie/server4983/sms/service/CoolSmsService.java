package team.dankookie.server4983.sms.service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import team.dankookie.server4983.sms.dto.SmsCertificationNumber;

@RequiredArgsConstructor
@Service
public class CoolSmsService {

    @Value("${coolsms.api.key}")
    private String apiKey;
    @Value("${coolsms.api.secret}")
    private String apiSecretKey;
    @Value("${naver-cloud-sms.senderPhone}")
    private String senderPhone;


    private DefaultMessageService messageService;

    //  @PostConstruct
    private void init() {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecretKey,
                "https://api.coolsms.co.kr");
    }

    public SingleMessageSentResponse sendMessage(String to, String content) {
        Message message = new Message();

        message.setFrom(senderPhone);
        message.setTo(to);
        message.setText(content);

        return this.messageService.sendOne(new SingleMessageSendingRequest(message));
    }


    public SmsCertificationNumber sendCertificationNumberToPhoneNumber(String phoneNumber) {
        Message message = new Message();

        int randomNumber = ThreadLocalRandom.current().nextInt(100000, 1000000);
        String content = "안녕하세요! 4983 입니다. \n인증번호는 " + randomNumber + " 입니다.";

        message.setFrom(senderPhone);
        message.setTo(phoneNumber);
        message.setText(content);

        this.messageService.sendOne(new SingleMessageSendingRequest(message));

        return SmsCertificationNumber.of(String.valueOf(randomNumber));
    }


    public void sendAdminToSms(String content) {
        List<String> adminPhoneNumberList = List.of("01044873122", "01096100950", "01073730246",
                "01032910525");

        Message message = new Message();
        for (String phoneNumber : adminPhoneNumberList) {
            message.setFrom(senderPhone);
            message.setTo(phoneNumber);
            message.setText(content);
            this.messageService.sendOne(new SingleMessageSendingRequest(message));
        }
    }
}
