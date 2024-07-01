package team.dankookie.server4983.member.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PhoneNumberDuplicateResponse {
    private final boolean phoneNumberDuplicate;

    public static PhoneNumberDuplicateResponse of(boolean isDuplicate) {
        return new PhoneNumberDuplicateResponse(isDuplicate);
    }
}
