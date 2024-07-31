package University.Dormitory.web.dto.SignOutDTO;

import lombok.Builder;
import lombok.Getter;

public class SignOutResponseDTO {
    @Builder
    @Getter
    public static class SignOutDTO {
        boolean isSuccess;
        String message;
    }
}
