package University.Dormitory.web.dto.SignUpDTO;


import University.Dormitory.domain.Enum.Authority;
import University.Dormitory.domain.Enum.Dormitory;
import jakarta.validation.constraints.NotNull;
import lombok.*;

public class SignUpRequestDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignUpDto {
        @NotNull
        long userId;
        @NotNull
        Dormitory dormitory;
        @NotNull
        String authority;
        @NotNull
        String name;
    }
}