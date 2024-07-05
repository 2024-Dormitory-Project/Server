package University.Dormitory.web.dto.SignOutDTO;

import lombok.Getter;

public class SignOutRequestDTO {
    @Getter
    public static class SignOutDTO {
        int userId;
        String name;
    }
}