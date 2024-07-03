package University.Dormitory.web.dto.SignInDTO;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

public class SignInRequestDTO {
    @Setter
    @Getter
    public static class SignInDto {
        @NotNull
        private int userId;
        @NotNull
        private String password;
    }
}
