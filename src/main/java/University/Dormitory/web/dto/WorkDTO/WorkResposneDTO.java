package University.Dormitory.web.dto.WorkDTO;

import lombok.Builder;

public class WorkResposneDTO {
    @Builder
    public static class workResult {
        Boolean isSuccess;
        String message;
    }

    @Builder
    public static class exchangework {
        String status;
        String applicant;
        String acceptor;
        String originDate;
        String changeDate;
    }
}
