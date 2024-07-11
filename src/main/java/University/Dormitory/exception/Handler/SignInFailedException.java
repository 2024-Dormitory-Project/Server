package University.Dormitory.exception.Handler;

public class SignInFailedException extends RuntimeException{
    public SignInFailedException(String Message) {
        super(Message);
    }
}
