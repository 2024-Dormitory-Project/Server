package University.Dormitory.web.dto.SignInDTO;

import lombok.*;

public class SignInResponseDTO {
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @Getter
    @Setter
    @Builder
    public static class SignInSuccess {
        private String message;
        private String token;
    }

    @Builder
    @Setter
    public static class SignInFail {
        private String message;
    }

}