package University.Dormitory.web.dto.MainPageDTO;

import University.Dormitory.domain.WorkScheduleChange;
import University.Dormitory.repository.CustomRepository;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class MainResponseDTO {
    public static class mainDto {
            List<Tuple> scheduleInfo;
            List<WorkScheduleChange> changedList;
    }
}
