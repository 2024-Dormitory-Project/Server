package University.Dormitory.exception.Handler;

import lombok.Getter;

@Getter
public class UndefinedAuthorityException extends RuntimeException {
    String Message;
    String Error;
    public UndefinedAuthorityException(String message) {
        super(message);
        this.Error = "UNDEFINED_AUTHORITY";
        this.Message = message;
    }
}