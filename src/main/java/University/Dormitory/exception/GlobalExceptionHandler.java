package University.Dormitory.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
//    @ExceptionHandler(PasswordNotMatchException.class)
//    public ApiResponse<SignInResponseDTO.SignInFail> handlePasswordNotMatchException(PasswordNotMatchException e) {
//        log.error("로그인 실패: {}", e.getMessage());
//        SignInResponseDTO.SignInFail result = SignInResponseDTO.SignInFail.builder()
//                .message(e.getMessage())
//                .build();
//        return ApiResponse.onFailure(WRONG_PASSWROD.getCode(),e.getMessage(), result);
//    }
//    @ExceptionHandler(PKDuplicateException.class)
//    public ApiResponse<Void> handlePKDuplicate(PKDuplicateException e) {
//        log.error("PK중복 오류: {}", e.getMessage());
//        return ApiResponse.onFailure(PK_DUPLICATE.getCode(), e.getMessage());
//    }
//    @ExceptionHandler(UserNotFoundException.class)
//    public ApiResponse<Void> handleUserNotFound(UserNotFoundException e) {
//        return ApiResponse.onFailure(USER_NOT_FOUND.getCode(),e.getMessage());
//    }

//    @ExceptionHandler
//    public ApiResponse<Void> handleTooLong(TooLongException e) {
//        return ApiResponse.onFailure()
//    }
}
