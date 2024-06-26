package University.Dormitory.apiPayLoad.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    _FORBIDDEN(HttpStatus.FORBIDDEN, "CODE403", "권한이 없습니다."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "CODE4001", "사용자가 없습니다."),
    WRONG_PASSWROD(HttpStatus.BAD_REQUEST, "CODE4002", "비밀번호가 틀립니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;


}
