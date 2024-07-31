package University.Dormitory.exception.Handler;


import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException{
    String Message;
    String Error;
    public UserNotFoundException(String Message) {
        super(Message);
        this.Error = "USER_NOT_FOUND";
        this.Message = Message;
    }
}
