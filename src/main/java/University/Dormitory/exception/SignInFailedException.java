package University.Dormitory.exception;

public class SignInFailedException extends RuntimeException{
    public SignInFailedException(String Message) {
        super(Message);
    }
}
