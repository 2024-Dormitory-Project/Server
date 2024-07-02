package University.Dormitory.exception;

public class SignInFailException extends RuntimeException{
    public SignInFailException(String message) {
        super(message);
    }
}
