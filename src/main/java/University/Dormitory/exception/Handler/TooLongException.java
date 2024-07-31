package University.Dormitory.exception.Handler;

import lombok.Getter;

@Getter
public class TooLongException extends RuntimeException{
    String Message;
    String Error;
    public TooLongException(String message) {
        super(message);
        this.Error = "TOO_LONG";
        this.Message = message;
    }
}
