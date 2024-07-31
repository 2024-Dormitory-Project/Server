package University.Dormitory.web.dto.UserDTO;

import University.Dormitory.domain.Enum.Authority;
import University.Dormitory.domain.Enum.Dormitory;
import lombok.Builder;

public class UserResponseDTO {
    @Builder
    public static class userinfo {
        long userId;
        Dormitory dormitory;
        Authority authority;
    }
}
