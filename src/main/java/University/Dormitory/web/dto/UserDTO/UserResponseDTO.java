package University.Dormitory.web.dto.UserDTO;

import University.Dormitory.domain.Enum.Authority;
import University.Dormitory.domain.Enum.Dormitory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class UserResponseDTO {
    @Builder
    @Getter
    public static class userinfo {
        long userId;
        Dormitory dormitory;
        Authority authority;
        LocalDate joinDate;
        String password;
    }
}
