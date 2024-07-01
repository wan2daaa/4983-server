package team.dankookie.server4983.sms.dto;

public record SmsCertificationNumber(
        String certificationNumber
) {

    public static SmsCertificationNumber of(String certificationNumber) {
        return new SmsCertificationNumber(certificationNumber);
    }
}
