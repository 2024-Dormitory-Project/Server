package University.Dormitory.exception;

import University.Dormitory.apiPayLoad.ApiResponse;
import University.Dormitory.web.dto.SignInDTO.SignInResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static University.Dormitory.apiPayLoad.code.ErrorCode.WRONG_PASSWROD;

@Slf4j
@RestControllerAdvice
public class GlobalExceptuonHandler {
    @ExceptionHandler(SignInFailException.class)
    public ApiResponse<SignInResponseDTO.SignInFail> handleSignInFailException(SignInFailException e) {
        log.error("로그인 실패: {}", e.getMessage());
        SignInResponseDTO.SignInFail result = SignInResponseDTO.SignInFail.builder()
                .message(e.getMessage())
                .build();
        return ApiResponse.onFailure(WRONG_PASSWROD.getCode(),e.getMessage(), result);
    }

}
