package University.Dormitory.exception.ExceptionResponseDto;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private final String errorCode;
    private final String message;
    private final boolean isSuccess = false;

    public ErrorResponse(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
