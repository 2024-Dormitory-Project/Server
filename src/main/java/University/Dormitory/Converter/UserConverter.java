package University.Dormitory.Converter;

import University.Dormitory.domain.Enum.Authority;
import University.Dormitory.domain.User;
import University.Dormitory.exception.Handler.UndefinedAuthorityException;
import University.Dormitory.web.dto.SignUpDTO.SignUpRequestDTO;

import java.time.LocalDate;

public class UserConverter {
    public static User toUser(SignUpRequestDTO.SignUpDto request) {
        Authority authority;
        String authorityString = String.valueOf(request.getAuthority());
        switch (authorityString) {
            case "사감":
                authority = Authority.ROLE_PERFECT;
                break;
            case "스케줄 관리 조교":
                authority = Authority.ROLE_SCHEDULE_ASSISTANT;
                break;
            case "조교":
                authority = Authority.ROLE_ASSISTANT;
                break;
            default:
                throw new UndefinedAuthorityException("정의되지 않은 권한입니다: " + authorityString);
        }

        return User.builder()
                .userId(request.getUserId())
                .name(request.getName())
                .password(request.getName())
                .joinDate(LocalDate.now())
                .dormitory(request.getDormitory())
                .authority(authority)
                .build();
    }
}