package University.Dormitory.exception.Handler;

import lombok.Getter;

@Getter
public class RefreshTokenInvalidException extends RuntimeException{
    String Message;
    String Error;
    public RefreshTokenInvalidException(String message) {
        super(message);
        this.Error = "REFRESH_TOKEN_INVALID";
        this.Message = message;
    }
}
