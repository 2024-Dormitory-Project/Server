package University.Dormitory.web.dto.WorkDateDTO;

import University.Dormitory.domain.WorkDate;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class WorkDateResponseDTO {

    public static class payRollDto{
        int thisMonth;
        int workDays;
        int workHours;
    }

    @Builder
    public static class calendarDto {
        List<LocalDate> workdates;
    }
    @Builder
    public static class changeHistory {
        String status;
        String applicant;
        String acceptor;
        LocalDateTime originDate;
        LocalDateTime changeDate;
    }
}
