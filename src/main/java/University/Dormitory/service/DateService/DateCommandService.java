package University.Dormitory.service.DateService;

import University.Dormitory.web.dto.WorkDateDTO.WorkDateRequestDTO;

public interface DateCommandService {
//    지금까지 내가 일한 시간 쿼리
    int workHour(int year, int workMonth, int userId);

//    지금까지 내가 일한 날짜 쿼리
    int workDays(int year, int workMonth, int userId);
}
