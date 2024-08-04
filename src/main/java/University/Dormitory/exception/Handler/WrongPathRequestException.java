package University.Dormitory.exception.Handler;

import lombok.Getter;

@Getter
public class WrongPathRequestException extends RuntimeException{
    String Message;
    String Error;
    public WrongPathRequestException(String message, String path) {
        super(message);
        this.Message = path + message;
        this.Error = "WRONG_PATH";
    }
}
