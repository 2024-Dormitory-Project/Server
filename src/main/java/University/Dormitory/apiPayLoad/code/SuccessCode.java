package University.Dormitory.apiPayLoad.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode {
    _OK(HttpStatus.OK, "CODE200", "성공입니다.")
    ;
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;



}
