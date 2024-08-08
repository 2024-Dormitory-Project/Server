package University.Dormitory.web.dto.ScheduleDTO;

import University.Dormitory.domain.Enum.Dormitory;
import lombok.Builder;
import lombok.Getter;

public class ScheduleRequestDTO {
    @Getter
    public static class todayWorkersDetailDto {
        int month;
        int year;
        int day;
    }

    @Getter
    public static class actualWorkTimeDto {
        int dormitory;
        int year;
        int month;
        String name;
    }

    @Getter
    public static class changeActualScheduleTime {
        int originYear;
        int originMonth;
        int originDay;
        int originHour;
        int originMin;
        int dormitoryNum;
        int year;
        int month;
        int day;
        int hour;
        int min;
        String name;
    }
}
