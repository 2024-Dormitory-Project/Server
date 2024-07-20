package University.Dormitory.web.dto.SignOutDTO;

import lombok.Builder;

public class SignOutResponseDTO {
    @Builder
    public static class SignOutDTO {
        boolean isSuccess;
        String message;
    }
}
