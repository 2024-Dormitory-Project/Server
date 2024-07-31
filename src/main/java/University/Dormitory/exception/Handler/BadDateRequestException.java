package University.Dormitory.exception.Handler;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BadDateRequestException extends RuntimeException {
    private final String Message;
    private final String Error;
    public BadDateRequestException(String message) {
        super(message);
        this.Error = "BAD_DATE_REQUEST";
        this.Message = message;
    }
}
