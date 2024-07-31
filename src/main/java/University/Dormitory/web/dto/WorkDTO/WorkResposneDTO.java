package University.Dormitory.web.dto.WorkDTO;

import lombok.Builder;
import lombok.Getter;

public class WorkResposneDTO {
    @Getter
    @Builder
    public static class workResult {
        Boolean isSuccess;
        String message;
    }

    @Builder
    @Getter
    public static class exchangework {
        String status;
        String applicant;
        String acceptor;
        String originDate;
        String changeDate;
    }
}
