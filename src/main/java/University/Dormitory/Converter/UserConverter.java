package University.Dormitory.Converter;

import University.Dormitory.domain.User;
import University.Dormitory.web.dto.SignUpDTO.SignUpRequestDTO;

import java.time.LocalDate;

public class UserConverter {
    public static User toUser(SignUpRequestDTO.SignUpDto request) {
        return User.builder()
                .userId(request.getUserId())
                .name(request.getName())
                .password(request.getPassword())
                .joinDate(LocalDate.now())
                .dormitory(request.getDormitory())
                .build();
    }
}
