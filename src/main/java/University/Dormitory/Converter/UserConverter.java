package University.Dormitory.Converter;

import University.Dormitory.domain.Enum.Authority;
import University.Dormitory.domain.User;
import University.Dormitory.exception.UndefinedAuthorityException;
import University.Dormitory.web.dto.SignUpDTO.SignUpRequestDTO;

import java.time.LocalDate;

import java.time.LocalDate;

public class UserConverter {
    public static User toUser(SignUpRequestDTO.SignUpDto request) {
        Authority authority;
        String authorityString = request.getAuthority();
        switch (authorityString) {
            case "사감":
                authority = Authority.PERFECT;
                break;
            case "스케줄 관리 조교":
                authority = Authority.SCHEDULE_ASSISTANT;
                break;
            case "조교":
                authority = Authority.ASSISTANT;
                break;
            default:
                throw new UndefinedAuthorityException("정의되지 않은 권한입니다: " + authorityString);
        }

        return User.builder()
                .userId(request.getUserId())
                .name(request.getName())
                .password(request.getPassword())
                .joinDate(LocalDate.now())
                .dormitory(request.getDormitory())
                .authority(authority)
                .build();
    }
}
