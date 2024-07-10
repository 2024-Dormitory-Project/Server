package University.Dormitory.web.dto.UserDTO;

public class UserRequestDTO {
    public static class findUserAuthorityDto {
        int userId;
    }

    public static class changeUserAuthorityDto {
        int userId;
        String updateAuthoirty;
    }
}
