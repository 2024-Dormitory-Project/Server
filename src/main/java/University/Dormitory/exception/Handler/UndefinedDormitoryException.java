package University.Dormitory.exception.Handler;

import lombok.Getter;

@Getter
public class UndefinedDormitoryException extends RuntimeException{
    String Message;
    String Error;
    public UndefinedDormitoryException(String message) {
        super(message);
        this.Error = "UNDEFINED_DORMITORY";
        this.Message = message;
    }
}
