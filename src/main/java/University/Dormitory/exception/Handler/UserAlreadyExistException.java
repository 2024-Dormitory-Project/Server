package University.Dormitory.exception.Handler;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserAlreadyExistException extends RuntimeException{
    private final String Message;
    private final String Error;
    public UserAlreadyExistException(String message) {
        super(message);
        this.Error = "USER_ALREADY_EXIST";
        this.Message = message;
    }
}
