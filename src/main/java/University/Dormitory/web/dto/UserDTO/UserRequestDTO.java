package University.Dormitory.web.dto.UserDTO;

import lombok.Getter;
import lombok.Setter;

public class UserRequestDTO {
    @Getter
    public static class changeUserAuthorityDto {
        long studentNumber;
        String authority;
        String name;
        int dormitoryNum;
    }

    @Getter
    @Setter
    public static class Token {
        String token;
    }
}
