package University.Dormitory.exception.Handler;

import lombok.Getter;

@Getter
public class UndefinedWorkDate extends RuntimeException{
    String Message;
    String Error;
    public UndefinedWorkDate(String Message) {
        super(Message);
        this.Error = "WORK_NOT_FOUND";
        this.Message = Message;
    }
}
