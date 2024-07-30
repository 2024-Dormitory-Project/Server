package University.Dormitory.exception.Handler;

import lombok.Getter;

@Getter
public class PKDuplicateException extends RuntimeException{
    String Message;
    String Error;
    public PKDuplicateException(String message) {
        super(message);
        this.Message = message;
        this.Error = "PK_DUPLICATE";
    }
}
