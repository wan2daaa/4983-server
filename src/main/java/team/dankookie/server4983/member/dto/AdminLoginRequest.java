package team.dankookie.server4983.member.dto;

public record AdminLoginRequest(
        String id,
        String password
) {

    public static LoginRequest of(String id, String password) {
        return new LoginRequest(id, password);
    }

}

