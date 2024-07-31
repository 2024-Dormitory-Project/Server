package University.Dormitory.web.dto.WorkDTO;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public class WorkRequestDTO {
    @Getter
    public static class giveWorkDto {
        String userName;
        String year;
        String month;
        String day;
        String hour;
        String min;
    }

    @Getter
    public static class exchangeWorkDto {
        int year1;
        int month1;
        int day1;
        int hour1;
        int min1;
        String userName2;
        int year2;
        int month2;
        int day2;
        int hour2;
        int min2;
    }

    @Getter
    public static class worker {
        String name;
        List<String> times;
    }
}