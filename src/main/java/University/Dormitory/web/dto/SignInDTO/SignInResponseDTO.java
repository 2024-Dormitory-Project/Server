package University.Dormitory.web.dto.SignInDTO;

import lombok.*;

public class SignInResponseDTO {
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @Getter
    @Setter
    @Builder
    public static class SignInDto {
        private String token;
        private String name;
        private int userId;
    }

    @Builder
    @Setter
    public static class SignInFail {
        private String message;

        public SignInFail(String message) {
            this.message = message;
        }
    }

}