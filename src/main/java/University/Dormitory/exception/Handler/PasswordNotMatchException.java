package University.Dormitory.exception.Handler;

import lombok.Getter;

@Getter
public class PasswordNotMatchException extends RuntimeException{
    String Message;
    String Error;
    public PasswordNotMatchException(String message) {
        super(message);
        this.Message = message;
        this.Error = "PASSWORD_NOT_MATCH";
    }
}
