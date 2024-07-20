package University.Dormitory.exception.Handler;

public class RefreshTokenInvalidException extends RuntimeException{
    public RefreshTokenInvalidException(String message) {
        super(message);
    }
}
