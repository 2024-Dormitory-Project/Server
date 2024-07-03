package University.Dormitory.web.dto.SignUpDTO;


import University.Dormitory.domain.Enum.Dormitory;
import jakarta.validation.constraints.NotNull;
import lombok.*;

public class SignUpRequestDTO {

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignUpDto {
        @NotNull
        int userId;
        @NotNull
        Dormitory dormitory;
        @NotNull
        String password;
        @NotNull
        String authority;
        @NotNull
        String Name;
    }
}
