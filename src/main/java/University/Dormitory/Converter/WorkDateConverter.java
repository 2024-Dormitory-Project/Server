package University.Dormitory.Converter;

import University.Dormitory.domain.WorkDate;
import lombok.Builder;

import java.time.LocalDateTime;

public class WorkDateConverter {
    @Builder
    public static WorkDate SaveWorkDate(int userId, LocalDateTime startTime, LocalDateTime leaveTime) {
        return WorkDate.builder()
                .scheduledLeaveTime(leaveTime)
                .scheduledStartTime(startTime)
                .Reason(null)
                .actualLeaveTime(null)
                .actualStartTime(null)
                .build();
    }
}