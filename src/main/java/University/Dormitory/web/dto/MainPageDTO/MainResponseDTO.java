package University.Dormitory.web.dto.MainPageDTO;

import University.Dormitory.domain.WorkScheduleChange;
import University.Dormitory.repository.CustomRepository;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.querydsl.core.Tuple;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


public class MainResponseDTO {
    @Builder
    @Getter
    public static class TimeDto {
        int day;
        int time;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Work{
        Boolean isSuccess;
        String message;
    }
}
