package University.Dormitory.service.DormitoryService;

import University.Dormitory.domain.Enum.Dormitory;
import University.Dormitory.repository.CustomRepository;

import java.time.LocalDate;
import java.util.Map;

public interface DormitoryCommandService {
    //    N기숙사의 근무자 보기
    Map<String, CustomRepository.WorkTime> viewDormitoryWorkers(LocalDate date, Dormitory dormitory);

    //    우편근무자 보기
    String viewPostWorker(LocalDate date);
}
