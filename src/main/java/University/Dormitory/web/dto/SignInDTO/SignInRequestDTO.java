package University.Dormitory.web.dto.SignInDTO;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class SignInRequestDTO {
    @Getter
    public static class SignInDto {
        @NotNull
        private int UserId;
        @NotNull
        private String password;
    }
}
