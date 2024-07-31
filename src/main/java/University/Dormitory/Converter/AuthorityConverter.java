package University.Dormitory.Converter;

import University.Dormitory.domain.Enum.Authority;
import University.Dormitory.exception.Handler.UndefinedAuthorityException;

public class AuthorityConverter {
    public static Authority toAuthority(String authorityString) {
        switch (authorityString) {
            case "사감":
                return Authority.PERFECT;
            case "스케줄 관리 조교":
                return Authority.SCHEDULE_ASSISTANT;
            case "조교":
                return Authority.ASSISTANT;
            default:
                throw new UndefinedAuthorityException("정의되지 않은 권한입니다: " + authorityString);
        }
    }
}
