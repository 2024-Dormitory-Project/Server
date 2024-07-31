package University.Dormitory.service.DateService;

import java.time.LocalDate;
import java.util.List;

public interface DateCommandService {
//    지금까지 내가 일한 시간 쿼리
    int workHour(int year, int workMonth, long userId);

//    지금까지 내가 일한 날짜 쿼리
    int workDays(int year, int workMonth, long userId);

//    이번 달 나의 근무일 보기
    List<LocalDate> myWorkdays (int year, int month, long userId);

}
