package University.Dormitory.exception.Handler;

import lombok.Getter;

@Getter
public class InvalidTokenException extends RuntimeException {
    private final String Error;
    private final String message;

    public InvalidTokenException(String message) {
        super(message);
        this.Error = "INVALID_TOKEN";
        this.message = message;
    }
}
