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
        private boolean isSuccess;
        private String message;
        private String accessToken;
        private String refreshToken;
    }

    @Builder
    @Setter
    @Getter
    public static class SignInFail {
        private String message;
    }

    @Builder
    @Setter
    @Getter
    public static class RefreshToken {
        private String token;
    }

    @Builder
    @Setter
    @Getter
    public static class NewAccessToken {
        private boolean isSuccess;
        private String accessToken;
    }

}