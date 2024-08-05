package University.Dormitory.web.dto.ScheduleDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScheduleResponseDTO {
    @Builder
    @Getter
    public static class actualWorkTime {
        LocalDate date;
        LocalDateTime scheduleTime;
        LocalDateTime actualTime;
        String name;
        String Reason;
    }
}
