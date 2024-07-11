package University.Dormitory.exception.Handler;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String Message) {
        super(Message);
    }
}
