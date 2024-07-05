package University.Dormitory.service.DormitoryService;

import University.Dormitory.domain.Enum.Dormitory;

import java.time.LocalDate;

public interface DormitoryCommandService {
//    N기숙사의 근무자 보기
    String viewDormitoryWorkers(LocalDate date, Dormitory dormitory);
//    우편근무자 보기
    String viewPostWorker(LocalDate date);
}
